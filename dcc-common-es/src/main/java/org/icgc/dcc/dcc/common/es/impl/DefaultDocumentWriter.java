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
package org.icgc.dcc.dcc.common.es.impl;

import static com.google.common.base.Throwables.propagate;
import static org.elasticsearch.client.Requests.indexRequest;
import static org.elasticsearch.common.xcontent.XContentType.SMILE;
import static org.icgc.dcc.common.core.util.Formats.formatCount;
import static org.icgc.dcc.dcc.common.es.util.BulkProcessorConfiguration.getBulkSize;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.icgc.dcc.dcc.common.es.core.DocumentWriter;
import org.icgc.dcc.dcc.common.es.json.JacksonFactory;
import org.icgc.dcc.dcc.common.es.model.IndexDocument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Output destination for {@link DefaultDocument} instances to be written.
 */
@Slf4j
public class DefaultDocumentWriter implements DocumentWriter {

  /**
   * Constants.
   */
  private static final ObjectWriter BINARY_WRITER = JacksonFactory.getObjectWriter();

  /**
   * Meta data.
   */
  @Getter
  private final String indexName;

  /**
   * Helps to track log records related to this particular writer.
   */
  private final String writerId;
  private final ByteSizeValue bulkSize;

  /**
   * Batching state.
   */
  @Getter
  private final IndexingState indexingState;
  private final BulkProcessor processor;

  // Holding a reference to the client to be able to close it, as the caller might not have reference to it.
  private final Client client;

  /**
   * Status.
   */
  private int documentCount;

  public DefaultDocumentWriter(DocumentWriterContext context) {
    this.indexName = context.getIndexName();
    this.writerId = context.getWriterId();
    this.indexingState = context.getIndexingState();
    this.processor = context.getBulkProcessor();
    this.client = context.getClient();
    this.bulkSize = getBulkSize(context.getBulkSizeMb());
    log.info("[{}] Created ES document writer.", writerId);
  }

  @Override
  public void write(@NonNull IndexDocument document) throws IOException {
    byte[] source = createSource(document.getSource());
    write(document.getId(), document.getType(), source);
  }

  protected void write(String id, IndexDocumentType type, byte[] source) {
    if (isBigDocument(source.length)) {
      processor.flush();
    }

    val request = createRequest(id, type, source);
    processor.add(request);
    documentCount++;
  }

  @Override
  public void close() throws IOException {
    log.debug("Trying to close the document writer...");
    // Initiate an index request which will set the pendingBulkRequest
    processor.flush();

    log.info("[{}] Closing bulk processor...", writerId);
    indexingState.waitForPendingRequests();
    processor.close();
    client.close();
    log.info("[{}] Finished indexing {} documents", writerId, formatCount(documentCount));
  }

  protected static byte[] createSource(Object document) {
    try {
      return BINARY_WRITER.writeValueAsBytes(document);
    } catch (JsonProcessingException e) {
      throw propagate(e);
    }
  }

  private IndexRequest createRequest(String id, IndexDocumentType type, byte[] source) {
    return indexRequest(indexName).type(type.getIndexType()).id(id).contentType(SMILE).source(source);
  }

  private boolean isBigDocument(int length) {
    return length > bulkSize.getBytes();
  }

}
