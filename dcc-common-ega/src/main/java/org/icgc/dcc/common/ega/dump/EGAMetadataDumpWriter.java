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
package org.icgc.dcc.common.ega.dump;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET;
import static com.google.common.base.Preconditions.checkState;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.stream.Stream;

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
public class EGAMetadataDumpWriter {

  /**
   * Constants.
   */
  private static final ObjectMapper MAPPER = DEFAULT.configure(AUTO_CLOSE_TARGET, false);

  @SneakyThrows
  public void write(@NonNull File file, Stream<EGADatasetDump> datasets) {
    val watch = Stopwatch.createStarted();

    @Cleanup
    val writer = new PrintWriter(file);

    val errors = Lists.<Exception> newArrayList();
    datasets.forEach(dataset -> {
      String datasetId = dataset.getDatasetId();
      try {
        log.info("Processing data set: {}", datasetId);
        writeDataset(dataset, writer);
        writer.println();
      } catch (Exception e) {
        log.error("Error processing data set {}: {}", datasetId, e);
        errors.add(e);
      }
    });

    log.info("Finished writing data sets in {}", watch);

    log.info("\n\n");
    log.info("Errors:");
    for (val error : errors) {
      log.error("- {}", error.getMessage());
    }

    checkState(errors.isEmpty(), "Error writing %s: %s", file, errors);
  }

  @SneakyThrows
  private void writeDataset(EGADatasetDump dataset, Writer writer) {
    try {
      MAPPER.writeValue(writer, dataset);
    } catch (Exception e) {
      throw new RuntimeException(
          "Could not read metadata archive for data set " + dataset.getDatasetId() + " associated with project(s) "
              + dataset.getProjectCodes() + ": " + e.getMessage(),
          e);
    }
  }

}
