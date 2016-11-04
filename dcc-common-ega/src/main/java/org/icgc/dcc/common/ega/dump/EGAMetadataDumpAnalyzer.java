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

import static java.util.stream.Collectors.toList;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.object;
import static org.icgc.dcc.common.core.util.Formats.formatCount;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.icgc.dcc.common.core.util.stream.Streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EGAMetadataDumpAnalyzer {

  /**
   * Entry-point
   */
  @SneakyThrows
  public static void main(String[] args) {
    val dumpFile = new File(args[0]);
    val reportFile = new File(args[1]);

    @Cleanup
    val writer = new PrintWriter(reportFile);

    log.info("Writing report to: {}", reportFile.getAbsolutePath());
    val analyzer = new EGAMetadataDumpAnalyzer(writer);
    analyzer.analyze(dumpFile);
  }

  /**
   * Dependencies
   */
  @NonNull
  private final PrintWriter report;

  public void analyze(File dumpFile) {
    val datasets = new EGAMetadataDumpReader().read(dumpFile);

    datasets.forEach(this::analyzeDataset);
  }

  private void analyzeDataset(ObjectNode dataset) {
    val files = Lists.<ObjectNode> newArrayList();
    val nestedFiles = Lists.<ObjectNode> newArrayList();

    log.info("Analyzing data set {}", dataset.get("datasetId"));
    files.addAll(resolveFiles(dataset));
    nestedFiles.addAll(resolveNestedFiles(dataset));

    log.info("Indexing...");
    val filesIndex = Multimaps.index(files, file -> file.path("fileName").textValue()).asMap();
    val nestedFilesIndex = Multimaps.index(nestedFiles, file -> file.get("fileName").textValue()).asMap();

    log.info("Joining...");
    val result = joinFiles(filesIndex, nestedFilesIndex);

    log.info("Reporting...");
    report(result);
  }

  private void report(Map<String, ObjectNode> result) {
    int i = 0;
    for (val record : result.values()) {
      report.println(record);
      i++;
    }

    log.info("{} total files", formatCount(i));
  }

  private static Map<String, ObjectNode> joinFiles(
      Map<String, Collection<ObjectNode>> filesIndex,
      Map<String, Collection<ObjectNode>> nestedFilesIndex) {
    val joined = Maps.<String, ObjectNode> newHashMap();

    int n = 0;
    val size = nestedFilesIndex.size();
    for (val nestedEntry : nestedFilesIndex.entrySet()) {
      if (++n % 1000 == 0) {
        log.info("Joined [{}/{}] rows", formatCount(n), formatCount(size));
      }

      val nestedFileName = nestedEntry.getKey();
      val nestedFiles = nestedEntry.getValue();
      for (val entry : filesIndex.entrySet()) {
        val fileName = entry.getKey();
        val files = entry.getValue();
        if (matchFileName(nestedFileName, fileName)) {
          val record = mergeFile(files, nestedFiles);

          joined.put(nestedFileName, record);
        }
      }
    }

    return joined;
  }

  private static ObjectNode mergeFile(Collection<ObjectNode> files, Collection<ObjectNode> nestedFiles) {
    val record = DEFAULT.createObjectNode();

    // Order important
    for (val file : files) {
      record.putAll(file);
    }
    for (val nestedFile : nestedFiles) {
      record.putAll(nestedFile);
    }

    return record;
  }

  private static boolean matchFileName(String nestedFileName, String fileName) {
    return fileName.contains(nestedFileName);
  }

  private static List<ObjectNode> resolveFiles(ObjectNode dataset) {
    return Streams.stream(dataset.path("files"))
        .map(file -> (ObjectNode) file)
        .map(file -> {
          return object()
              .with("datasetId", file.get("fileDataset").textValue())
              .with("fileId", file.get("fileID").textValue())
              .with("fileName", file.get("fileName").textValue())
              .with("filePath", file.get("fileName").textValue())
              .with("fileSize", file.get("fileSize").textValue())
              .with("fileStatus", file.get("fileStatus").textValue())
              .end();
        })
        .collect(toList());
  }

  private static List<ObjectNode> resolveNestedFiles(ObjectNode dataset) {
    return dataset.findValues("FILE").stream()
        .flatMap(file -> file.isArray() ? Streams.<JsonNode> stream(file) : Stream.<JsonNode> of(file))
        .map(file -> (ObjectNode) file)
        .map(file -> {
          if (file.has("filename")) {
            file.put("fileName", file.get("filename").textValue());
          }
          return object()
              .with("fileName", file.get("fileName").textValue())
              .with("fileType", file.get("filetype").textValue())
              .with("checksum", file.get("checksum").textValue())
              .with("unencryptedChecksum", file.path("unencrypted_checksum").textValue())
              .end();
        })
        .collect(toList());
  }

}
