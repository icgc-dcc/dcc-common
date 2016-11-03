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
package org.icgc.dcc.common.ega.writer;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newTreeSet;
import static java.lang.System.lineSeparator;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.icgc.dcc.common.ega.client.EGAClient;
import org.icgc.dcc.common.ega.client.EGAFTPClient;
import org.icgc.dcc.common.ega.core.EGAProjectDatasets;
import org.icgc.dcc.common.ega.model.EGAMetadata;
import org.icgc.dcc.common.ega.model.EGAMetadataArchive;
import org.icgc.dcc.common.ega.reader.EGAMetadataArchiveReader;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility for writing all EGA metadata to a JSONL file.
 * 
 * @see https://www.ebi.ac.uk/ega/dacs/EGAC00001000010
 */
@Slf4j
@RequiredArgsConstructor
public class EGAMetadataWriter {

  /**
   * Constants.
   */
  private static final ObjectMapper MAPPER = DEFAULT.configure(AUTO_CLOSE_TARGET, false);
  private static final EGAMetadataArchiveReader ARCHIVE_READER = new EGAMetadataArchiveReader();

  /**
   * Dependencies.
   */
  private final EGAClient client;
  private final EGAFTPClient ftp;

  public EGAMetadataWriter() {
    this(createEGAClient(), createEGAFTPClient());
  }

  @SneakyThrows
  public void write(@NonNull File file) {
    val watch = Stopwatch.createStarted();

    @Cleanup
    val writer = new FileWriter(file);
    val datasetIds = getDatasetIds();
    val effectiveDatasetIds = newTreeSet(datasetIds);
    if (effectiveDatasetIds.size() != datasetIds.size()) {
      log.warn("Data sets include duplicates: {}", datasetIds);
    }

    int i = 1;
    val n = datasetIds.size();
    val errors = Lists.<Exception> newArrayList();

    log.info("Writing {} data sets to {}...", n, file);
    for (val datasetId : effectiveDatasetIds) {
      try {
        log.info("[{}/{}] Processing data set: {}", i++, n, datasetId);
        writeDataset(datasetId, client, writer);
        writer.write(lineSeparator());
      } catch (Exception e) {
        log.error("Error processing data set {}: {}", datasetId, e);
        errors.add(e);
      }
    }

    log.info("Finished writing {} data sets in {} with {} client timeouts, {} client reconnects and {} client errors",
        i, watch, client.getTimeoutCount(), client.getReconnectCount(), client.getErrorCount());

    for (val error : errors) {
      log.error("- {}", error.getMessage());
    }

    checkState(errors.isEmpty(), "Error writing %s: %s", file, errors);
  }

  protected List<String> getDatasetIds() {
    return client.getDatasetIds();
  }

  protected EGAMetadataArchive getMetadataArchive(String datasetId) {
    if (ftp.hasDatasetId(datasetId)) {
      val url = ftp.getMetadataURL(datasetId);
      return ARCHIVE_READER.read(datasetId, url);
    } else {
      return ARCHIVE_READER.read(datasetId);
    }
  }

  private void writeDataset(String datasetId, EGAClient client, FileWriter writer)
      throws IOException, JsonGenerationException, JsonMappingException {
    val metadata = getMetadataArchive(datasetId);
    val files = client.getDatasetFiles(datasetId);
    val projectCodes = EGAProjectDatasets.getDatasetProjectCodes(datasetId);

    val record = new EGAMetadata(datasetId, projectCodes, files, metadata);
    MAPPER.writeValue(writer, record);
  }

  private static EGAClient createEGAClient() {
    val client = new EGAClient();
    client.login();

    return client;
  }

  private static EGAFTPClient createEGAFTPClient() {
    return new EGAFTPClient();
  }

}
