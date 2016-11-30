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

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.repeat;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.array;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.object;
import static org.icgc.dcc.common.core.util.Formats.formatCount;
import static org.icgc.dcc.common.ega.core.EGAProjectDatasets.getDatasetProjectCodes;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.icgc.dcc.common.core.json.JsonNodeBuilders.ObjectNodeBuilder;
import org.icgc.dcc.common.core.util.stream.Streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Analyzer that reads a dump produced by {@link EGAMetadataDumper} to project JSONL report(s).
 * 
 * Bob: So it is quite possible that the exact same file (same md5) is submitted twice across 2 different runs for a
 * study, but then assigned a new EGAF id?
 * 
 * Audald: It is possible. This should not be the case, though. We have a sufficiently flexible submission (analysis)
 * pipeline for linking the file to all the relevant samples using a single object. The ideal situation would be that
 * each file (unique md5) is submitted only once. Actually, that is one of the reasons why runs and analysis can be then
 * reused for as many datasets as needed. However, it is physically impossible to check individually all the submissions
 * in order to ensure that data-metadata linkage is correct. Some files can be indeed submitted several times and linked
 * to several objects.
 *
 * Bob: Is it possible to reuse a previous run’s file and thus share the EGAF id?
 * 
 * Audald: Do you mean replacing a file (EGAF) by the same one (EGAF’) within a run? I am quite positive this is
 * technically possible. Definitely not a regular procedure.
 */
@Slf4j
@RequiredArgsConstructor
public class EGAMetadataDumpAnalyzer {

  /**
   * Entry-point
   */
  @SneakyThrows
  public static void main(String[] args) {
    val dumpFile = new File(args[0]);
    log.info("Reading dump from: {}", dumpFile.getAbsolutePath());
    val reportFile = new File(args[1]);
    log.info("Writing report to: {}", reportFile.getAbsolutePath());

    @Cleanup
    val writer = new PrintWriter(reportFile);

    val analyzer = new EGAMetadataDumpAnalyzer(writer);
    analyzer.analyze(dumpFile);
  }

  /**
   * Dependencies
   */
  @NonNull
  private final PrintWriter report;

  public void analyze(File dumpFile) {
    val watch = Stopwatch.createStarted();
    val datasets = read(dumpFile);

    log.info("Analyzing data sets...");
    val files = analyzeDatasets(datasets);

    log.info("Combining files...");
    val unique = combineFiles(files);

    log.info("Reporting...");
    report(unique);

    log.info("Finished analyzing in {}", watch);
  }

  private List<ObjectNode> analyzeDatasets(Stream<ObjectNode> datasets) {
    return datasets
        .map(this::analyzeDataset)
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private Collection<ObjectNode> analyzeDataset(ObjectNode dataset) {
    log.info(repeat("-", 100));
    log.info("Analyzing data set {}", dataset.get("datasetId"));
    log.info(repeat("-", 100));
    val files = analyzeDatasetFiles(dataset);

    return files;
  }

  private Collection<ObjectNode> analyzeDatasetFiles(ObjectNode dataset) {
    log.info("Resolving files...");
    val files = resolveFiles(dataset);
    val nestedFiles = resolveNestedFiles(dataset);
    val sampleFileMappings = resolveSampleFileMappings(dataset);

    val sampleDonorMappings = resolveSampleDonorMappings(dataset);
    val sampleProcessMappings = resolveSampleProcessMappings(dataset);
    val sampleTags = resolveSampleTags(dataset);

    log.info("Indexing files...");
    val filesIndex = groupBy(files, "fileName");
    val nestedFilesIndex = groupBy(nestedFiles, "fileName");
    val sampleFileIndex = groupBy(sampleFileMappings, "fileName");

    val sampleDonorIndex = groupBy(sampleDonorMappings, "sampleAlias");
    val sampleProcessIndex = groupBy(sampleProcessMappings, "sampleId");
    val sampleTagsIndex = groupBy(sampleTags, "sampleId");

    log.info("Joining files...");
    return joinFiles(
        filesIndex,
        nestedFilesIndex,
        sampleFileIndex,

        sampleDonorIndex,
        sampleProcessIndex,
        sampleTagsIndex);
  }

  private List<ObjectNode> combineFiles(List<ObjectNode> files) {
    val grouped = files.stream()
        .filter(file -> file.has("fileId")) // Some are unknown and can't be grouped reliably
        .collect(groupingBy(file -> {
          String fileId = file.path("fileId").textValue();
          if (!isNullOrEmpty(fileId)) return fileId;

          return "unknown";
        }));

    return grouped.values().stream().map(group -> {
      Set<String> datasetIds = newTreeSet();
      Set<String> projectIds = newTreeSet();
      Set<String> runIds = newTreeSet();
      Set<String> experimentIds = newTreeSet();
      Set<String> studyIds = newTreeSet();

      group.forEach(file -> {
        datasetIds.add(file.get("datasetId").textValue());
        file.get("projectId").forEach(projectId -> {
          projectIds.add(projectId.textValue());
        });
        file.get("runIds").forEach(runId -> {
          runIds.add(runId.textValue());
        });
        file.get("experimentIds").forEach(experimentId -> {
          experimentIds.add(experimentId.textValue());
        });
        file.get("studyIds").forEach(studyId -> {
          studyIds.add(studyId.textValue());
        });
      });

      return object(resolveFile(group))
          .with("datasetId", array(datasetIds))
          .with("projectId", array(projectIds))
          .with("runIds", array(runIds))
          .with("experimentIds", array(experimentIds))
          .with("studyIds", array(studyIds))
          .end();
    }).collect(toList());
  }

  private ObjectNode resolveFile(Collection<ObjectNode> group) {
    // Merge all
    val representative = getFirst(group, null).deepCopy();
    for (val file : group) {
      representative.putAll(file);
    }

    return representative;
  }

  private void report(List<ObjectNode> files) {
    for (val file : files) {
      report.println(file);
    }

    log.info("{} total files", formatCount(files));
  }

  private Collection<ObjectNode> joinFiles(
      Map<String, Collection<ObjectNode>> filesIndex,
      Map<String, Collection<ObjectNode>> nestedFilesIndex,
      Map<String, Collection<ObjectNode>> sampleFilesIndex,
      Map<String, Collection<ObjectNode>> sampleDonorIndex,
      Map<String, Collection<ObjectNode>> sampleProcessIndex,
      Map<String, Collection<ObjectNode>> sampleTagsIndex) {
    val joined = Lists.<ObjectNode> newArrayList();

    int n = 0;
    val size = nestedFilesIndex.size();

    val fileNames = Sets.<String> newTreeSet();
    fileNames.addAll(filesIndex.keySet());
    fileNames.addAll(nestedFilesIndex.keySet());
    fileNames.addAll(sampleFilesIndex.keySet());

    for (val fileName : fileNames) {
      if (++n % 1000 == 0) {
        log.info("Join examined [{}/{}] files", n, size);
      }

      val files = filesIndex.getOrDefault(fileName, emptyList());
      val nestedFiles = nestedFilesIndex.getOrDefault(fileName, emptyList());
      val sampleFiles = sampleFilesIndex.getOrDefault(fileName, emptyList());

      // Combine sources of file metadata
      val merged = mergeFile(
          files,
          nestedFiles,
          sampleFiles,

          sampleDonorIndex,
          sampleProcessIndex,
          sampleTagsIndex);

      joined.add(merged);
    }

    val missingFiles = difference(fileNames, filesIndex.keySet());
    if (!missingFiles.isEmpty()) log.warn("*** {} Missing files: {}",
        formatCount(missingFiles), missingFiles);

    val missingNestedFiles = difference(fileNames, nestedFilesIndex.keySet());
    if (!missingNestedFiles.isEmpty()) log.warn("*** {} Missing nested files: {}",
        formatCount(missingNestedFiles), missingNestedFiles);

    val missingSampleFiles = difference(fileNames, sampleFilesIndex.keySet());
    if (!missingSampleFiles.isEmpty()) log.warn("*** {} Missing sample files: {}",
        formatCount(missingSampleFiles), missingSampleFiles);

    log.info("=> Files: {}, Nested files: {}, Sample files: {}, Joined files: {}",
        formatCount(filesIndex), formatCount(nestedFilesIndex), formatCount(sampleFilesIndex), formatCount(joined));

    return joined;
  }

  private ObjectNode mergeFile(
      Collection<ObjectNode> files,
      Collection<ObjectNode> nestedFiles,
      Collection<ObjectNode> sampleFiles,

      Map<String, Collection<ObjectNode>> sampleDonorMappings,
      Map<String, Collection<ObjectNode>> sampleProcessMappings,
      Map<String, Collection<ObjectNode>> sampleTagsMappings) {
    val merged = object().end();

    // Order is important for these loops
    for (val sampleFile : sampleFiles) {
      merged.put("datasetId", sampleFile.get("datasetId").textValue());
      merged.put("projectId", sampleFile.get("projectId"));
      merged.put("fileId", sampleFile.get("fileId").textValue());
    }

    for (val file : files) {
      merged.putAll(file);
    }
    for (val nestedFile : nestedFiles) {
      merged.putAll(nestedFile);
    }

    val runIds = Sets.<String> newTreeSet();
    val experimentIds = Sets.<String> newTreeSet();
    val studyIds = Sets.<String> newTreeSet();

    val samples = Sets.<ObjectNode> newHashSet();
    for (val sampleMapping : sampleFiles) {
      val sampleId = sampleMapping.get("sampleId").textValue();
      val sampleTags = sampleTagsMappings.get(sampleId);
      val sampleEgaIds = sampleProcessMappings.get(sampleId);

      val tags = object().end();
      if (sampleTags != null) {
        for (val sampleTag : sampleTags) {
          tags.putAll((ObjectNode) sampleTag.get("tags"));
        }
      }

      val sampleAlias = sampleMapping.get("sampleAlias").textValue();
      val sampleDonors = sampleDonorMappings.get(sampleAlias);

      // First try from mapping
      String sampleDonorId = sampleDonors != null ? resolveFile(sampleDonors).get("donorId").textValue() : null;
      if (sampleDonorId == null) {
        // Try to find in tags
        if (tags.has("Donor ID")) {
          sampleDonorId = tags.get("Donor ID").textValue().trim();
        } else if (tags.has("Donor Id")) {
          sampleDonorId = tags.get("Donor Id").textValue().trim();
        } else if (tags.has("subject_id")) {
          sampleDonorId = tags.get("subject_id").textValue().trim();
        } else if (tags.has("donor_id")) {
          sampleDonorId = tags.get("donor_id").textValue().trim();
        }
      }

      String sampleType = tags.path("sample type").textValue();
      if (sampleType == null && tags.has("phenotype")) {
        sampleType = tags.get("phenotype").textValue().trim();
      }
      // PCAWG: Normalize
      if (tags.has("specimen_type")) {
        sampleType = tags.get("specimen_type").textValue().trim();
      }

      // PCAWG: Normalize
      if (tags.has("submitter_donor_id")) {
        sampleDonorId = tags.get("submitter_donor_id").textValue().trim();
      }

      // PCAWG: Normalize
      if (tags.has("icgc_project_code")) {
        // Promote to record
        merged.withArray("projectId").add(tags.get("icgc_project_code").textValue().trim());
      }

      // PCAWG: Normalize
      String submitterSampleId = sampleAlias;
      if (tags.has("submitter_sample_id")) {
        submitterSampleId = tags.get("submitter_sample_id").textValue().trim();
      }

      // Remove duplication
      tags.remove(ImmutableList.of(
          "Donor Id",
          "Donor ID",
          "donor_id",
          "Sample ID",
          "Sample Id",
          "subject_id",
          "sample type",
          "ENA-CHECKLIST"));

      if (sampleEgaIds != null) {
        sampleEgaIds.forEach(ids -> {
          runIds.add(ids.get("runId").textValue());
          experimentIds.add(ids.get("experimentId").textValue());
          studyIds.add(ids.get("studyId").textValue());
        });
      }

      samples.add(object()
          .with("sampleId", sampleId)
          .with("submitterSampleId", submitterSampleId)
          .with("sampleType", sampleType)
          .with("donorId", sampleDonorId)
          .with("tags", tags).end());
    }

    merged.put("samples", array().withNodes(samples).end());
    merged.put("runIds", array(runIds).end());
    merged.put("experimentIds", array(experimentIds).end());
    merged.put("studyIds", array(studyIds).end());

    return merged;
  }

  private static List<ObjectNode> resolveFiles(ObjectNode dataset) {
    return stream(dataset.path("files"))
        .map(file -> {
          String datasetId = file.get("fileDataset").textValue().trim();
          String fileName = file.get("fileName").textValue().trim();
          String normalizedFileName = normalizeFileName(fileName);

          return object()
              .with("projectId", getDatasetProjectCodes(datasetId))
              .with("datasetId", datasetId)
              .with("fileId", file.get("fileID").textValue().trim())
              .with("fileName", normalizedFileName)
              .with("filePath", file.get("fileName").textValue().trim())
              .with("fileSize", file.get("fileSize").asText().trim())
              .with("fileStatus", file.get("fileStatus").textValue().trim())
              .end();
        })
        .collect(toList());
  }

  private static List<ObjectNode> resolveNestedFiles(ObjectNode dataset) {
    val datasetId = dataset.get("datasetId").textValue().trim();
    return stream(dataset.findValues("FILE"))
        .map(file -> {
          // Accommodate differing record schemas:
          String fileName = firstNonNull(file.path("filename").textValue(), file.path("fileName").textValue()).trim();
          String normalizedFileName = normalizeFileName(fileName);

          return object()
              .with("datasetId", datasetId)
              .with("projectId", getDatasetProjectCodes(datasetId))
              .with("fileName", normalizedFileName)
              .with("fileType", file.path("filetype").textValue())
              .with("checksum", file.path("checksum").textValue())
              .with("unencryptedChecksum", file.path("unencrypted_checksum").textValue())
              .end();
        })
        .collect(toList());
  }

  private static List<ObjectNode> resolveSampleFileMappings(ObjectNode dataset) {
    val datasetId = dataset.get("datasetId").textValue().trim();
    return stream(dataset.path("metadata").path("mappings").path("Sample_File"))
        .map(file -> {
          String fileName = file.get("FILE_NAME").textValue().trim();
          String normalizedFileName = normalizeFileName(fileName);

          return object()
              .with("datasetId", datasetId)
              .with("projectId", getDatasetProjectCodes(datasetId))
              .with("fileId", file.path("FILE_ACCESSION").textValue().trim())
              .with("fileName", normalizedFileName)
              .with("sampleId", file.path("SAMPLE_ACCESSION").textValue().trim())
              .with("sampleAlias", file.path("SAMPLE_ALIAS").textValue().trim())
              .end();
        })
        .collect(toList());
  }

  private static List<ObjectNode> resolveSampleProcessMappings(ObjectNode dataset) {
    return stream(dataset.path("metadata").path("mappings").path("Study_Experiment_Run_sample"))
        .map(file -> {
          return object()
              .with("sampleId", file.path("EGA_SAMPLE_ID").textValue().trim())
              .with("runId", file.path("RUN_EGA_ID").textValue().trim())
              .with("experimentId", file.path("EXPERIMENT_EGA_ID").textValue().trim())
              .with("studyId", file.path("STUDY_EGA_ID").textValue().trim())
              .end();
        })
        .collect(toList());
  }

  private static List<ObjectNode> resolveSampleDonorMappings(ObjectNode dataset) {
    return stream(dataset.path("metadata").path("mappings").path("Run_Sample_meta_info"))
        .filter(file -> file.has("Sample ID"))
        .map(file -> {
          return object()
              .with("sampleAlias", file.get("Sample ID").textValue().trim())
              .with("donorId", file.get("Donor ID").textValue().trim())
              .end();
        })
        .collect(toList());
  }

  private static List<ObjectNode> resolveSampleTags(ObjectNode dataset) {
    val samples = dataset.path("metadata").path("samples");
    val sampleIds = samples.fieldNames();
    return Streams.stream(sampleIds)
        .map(sampleId -> {
          ObjectNode sample = (ObjectNode) samples.get(sampleId);
          ObjectNodeBuilder tags = object();
          for (JsonNode tag : sample.findParents("TAG")) {
            String tagName = tag.path("TAG").textValue().trim();
            String tagValue = tag.path("VALUE").asText().trim();
            if (isNullOrEmpty(tagValue)) {
              continue;
            }

            tags.with(tagName, tagValue);
          }

          return object()
              .with("sampleId", sampleId.trim())
              .with("tags", tags)
              .end();
        })
        .collect(toList());
  }

  private static Stream<ObjectNode> read(File dumpFile) {
    return new EGAMetadataDumpReader().read(dumpFile);
  }

  private static Stream<ObjectNode> stream(Iterable<? extends JsonNode> values) {
    return Streams.stream(values) // Can be array or object due to XML to JSON
        .flatMap(value -> value.isArray() ? Streams.<JsonNode> stream(value) : Stream.<JsonNode> of(value))
        .map(value -> (ObjectNode) value);
  }

  private static Map<String, Collection<ObjectNode>> groupBy(List<ObjectNode> values, String key) {
    return Multimaps.index(values, value -> value.get(key).textValue()).asMap();
  }

  private static String normalizeFileName(String fileName) {
    val trimmed = fileName.trim();
    val pathless = trimmed.substring(trimmed.lastIndexOf('/') + 1);
    return pathless.replaceAll("\\.(gpg|cip)$", "");
  }

}
