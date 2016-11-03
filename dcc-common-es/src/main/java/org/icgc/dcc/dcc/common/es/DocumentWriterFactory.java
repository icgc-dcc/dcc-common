/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.dcc.common.es;

import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.action.bulk.BulkProcessor.builder;

import java.net.URI;
import java.util.Random;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.icgc.dcc.dcc.common.es.core.DocumentWriter;
import org.icgc.dcc.dcc.common.es.impl.BulkProcessorListener;
import org.icgc.dcc.dcc.common.es.impl.ClusterStateVerifier;
import org.icgc.dcc.dcc.common.es.impl.DefaultDocumentWriter;
import org.icgc.dcc.dcc.common.es.impl.IndexingState;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class DocumentWriterFactory {

  private static final Random RANDOM_GENERATOR = new Random();
  private static final int BULK_ACTIONS = -1; // Unlimited

  public static DocumentWriter createDocumentWriter(DocumentWriterConfiguration configuration) {
    val client = configuration.client() != null ? configuration.client() : newClient(configuration.esUrl(), true);
    val writerId = createWriterId();
    val indexingState = new IndexingState(writerId);
    val clusterStateVerifier = new ClusterStateVerifier(client, configuration.indexName(), writerId, indexingState);
    val bulkProcessorListener = new BulkProcessorListener(clusterStateVerifier, indexingState, writerId);
    val bulkProcessor = createProcessor(client, bulkProcessorListener);
    return new DefaultDocumentWriter(client, configuration.indexName(), indexingState, bulkProcessor, writerId);
  }

  @SuppressWarnings("resource")
  private static Client newClient(@NonNull String esUri, boolean sniff) {
    val host = getHost(esUri);
    val port = getPort(esUri);
    val address = new InetSocketTransportAddress(host, port);

    log.info("Creating ES transport client from URI '{}': host = '{}', port = {}", new Object[] { esUri, host, port });
    return new TransportClient(createSettings(sniff)).addTransportAddress(address);
  }

  @SneakyThrows
  private static String getHost(String esUri) {
    return new URI(esUri).getHost();
  }

  @SneakyThrows
  private static int getPort(String esUri) {
    return new URI(esUri).getPort();
  }

  /**
   * Creates the client settings.
   * 
   * @see http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/client.htmls
   */
  private static Builder createSettings(boolean sniff) {
    return ImmutableSettings.settingsBuilder()

        // Increase the ping timeout from the 5s (default) to something larger to prevent transient
        // NoNodeAvailableExceptions
        .put("client.transport.ping_timeout", "20s")

        // The time to wait for a ping response from a node. Defaults to 5s.
        .put("client.transport.nodes_sampler_interval", "10s")

        // Enable / disable the client to sniff the rest of the cluster, and add those into its list of machines to use.
        // In this case, note that the IP addresses used will be the ones that the other nodes were started with (the
        // "publish" address)
        .put("client.transport.sniff", sniff);
  }

  private static String createWriterId() {
    val id = RANDOM_GENERATOR.nextInt(Integer.MAX_VALUE);

    return String.valueOf(Math.abs(id));
  }

  private static BulkProcessor createProcessor(Client client, BulkProcessorListener listener) {
    val bulkProcessor =
        builder(client, listener).setBulkActions(BULK_ACTIONS).setBulkSize(DefaultDocumentWriter.BULK_SIZE)
            .setConcurrentRequests(0).build();

    // Need to give back reference to bulkProcessor as it's reused for re-indexing of failed requests.
    listener.setProcessor(bulkProcessor);

    return bulkProcessor;
  }

}
