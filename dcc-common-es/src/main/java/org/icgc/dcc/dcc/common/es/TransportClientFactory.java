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

import java.net.InetAddress;
import java.net.URI;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class TransportClientFactory {

  /**
   * Creates a {@link TransportClient} with the {@code client.transport.sniff} set to {@code false}.
   */
  public static Client createClient(@NonNull String esUri) {
    return createClient(esUri, false);
  }

  /**
   * Creates a {@link TransportClient}.
   */
  @SneakyThrows
  public static Client createClient(@NonNull String esUri, boolean sniff) {
    val uri = new URI(esUri);
    val host = InetAddress.getByName(uri.getHost());
    val port = uri.getPort();
    val address = new InetSocketTransportAddress(host, port);

    log.info("Creating ES transport client from URI '{}': host = '{}', port = {}", new Object[] { esUri, host, port });
    return new PreBuiltTransportClient(createSettings(sniff)).addTransportAddress(address);
  }

  /**
   * Creates the client settings.
   * 
   * @see http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/client.htmls
   */
  private static Settings createSettings(boolean sniff) {
    return Settings.builder()

        // Increase the ping timeout from the 5s (default) to something larger to prevent transient
        // NoNodeAvailableExceptions
        .put("client.transport.ping_timeout", "20s")

        // The time to wait for a ping response from a node. Defaults to 5s.
        .put("client.transport.nodes_sampler_interval", "10s")

        // Enable / disable the client to sniff the rest of the cluster, and add those into its list of machines to use.
        // In this case, note that the IP addresses used will be the ones that the other nodes were started with (the
        // "publish" address)
        .put("client.transport.sniff", sniff)
        .build();
  }

}
