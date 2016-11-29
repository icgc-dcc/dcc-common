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
package org.icgc.dcc.common.ega.dataset;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.icgc.dcc.common.core.util.Splitters.PIPE;
import static org.icgc.dcc.common.core.util.Splitters.SEMICOLON;
import static org.icgc.dcc.common.core.util.Splitters.TAB;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import org.icgc.dcc.common.core.io.ForwardingInputStream;
import org.icgc.dcc.common.core.util.Splitters;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

/**
 * Reads an EGA mapping file into an in-memory representation.
 */
public class EGADatasetMappingReader {

  /**
   * Constants.
   */
  private static final Splitter EQUALS = Splitter.on('=').trimResults().omitEmptyStrings();

  @SneakyThrows
  public List<ObjectNode> read(@NonNull String fileName, @NonNull InputStream inputStream) {
    try {
      val lines = readLines(inputStream);
      if (isSemiColonDelimited(fileName)) {
        return readSemiColonDelimited(lines);
      } else if (isPipeDelimited(fileName)) {
        return readPipeDelimited(fileName, lines);
      } else {
        return readTabDelimited(fileName, lines);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error processing " + fileName, e);
    }
  }

  private static boolean isPipeDelimited(String fileName) {
    return fileName.endsWith("Sample_File.txt") ||
        fileName.endsWith("Run_Sample.txt") ||
        fileName.endsWith("Analysis_Sample.txt") ||
        fileName.endsWith("Study_Analysis_Sample.txt");
  }

  private static boolean isSemiColonDelimited(String fileName) {
    return fileName.equals("Run_Sample_meta_info.map");
  }

  @SuppressWarnings("resource")
  private static Stream<String> readLines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(new ForwardingInputStream(inputStream, false))).lines();
  }

  private static List<ObjectNode> readSemiColonDelimited(Stream<String> lines) {
    return lines
        .map(SEMICOLON.trimResults().omitEmptyStrings()::splitToList)
        .map(fields -> toObjectNode(fields))
        .collect(toImmutableList());
  }

  private static List<ObjectNode> readPipeDelimited(String fileName, Stream<String> lines) {
    val headers = getHeaders(fileName);

    return lines.map(PIPE.trimResults()::splitToList)
        .map(fields -> toObjectNode(headers, fields))
        .map(record -> {
          if (record.has("ATTRIBUTES")) {
            String text = record.get("ATTRIBUTES").textValue();
            record.put("ATTRIBUTES", parseAttributes(text));
          }

          return record;
        })
        .collect(toImmutableList());
  }

  private static List<ObjectNode> readTabDelimited(String fileName, Stream<String> lines) {
    val headers = getHeaders(fileName);

    return lines.map(TAB.trimResults()::splitToList)
        .map(fields -> toObjectNode(headers, fields))
        .collect(toImmutableList());
  }

  private static ObjectNode toObjectNode(List<String> fields) {
    val record = DEFAULT.createObjectNode();
    checkState(!fields.isEmpty(), "No fields present");

    for (val field : fields) {
      val parts = EQUALS.splitToList(field);
      record.put(parts.get(0), parts.get(1));
    }

    return record;
  }

  private static ObjectNode toObjectNode(List<String> headers, List<String> fields) {
    val record = DEFAULT.createObjectNode();
    checkState(headers.size() == fields.size(), "Header size (%s) not equal to fields size (%s) for mapping",
        headers.size(), fields.size());

    for (int i = 0; i < headers.size(); i++) {
      record.put(headers.get(i), fields.get(i));
    }

    return record;
  }

  private static ObjectNode parseAttributes(String text) {
    val attributes = DEFAULT.createObjectNode();
    if (isNullOrEmpty(text)) {
      return attributes;
    }
  
    List<String> values = EQUALS.trimResults().omitEmptyStrings().splitToList(text);
    for (int i = 0; i < values.size(); i += 2) {
      val key = values.get(i);
  
      if (i + 1 >= values.size()) {
        break;
      }
  
      val attributeValue = values.get(i + 1);
      if (attributeValue.contains(";")) {
        attributes.putPOJO(key, Splitters.SEMICOLON.split(values.get(i + 1)));
      } else {
        attributes.put(key, attributeValue);
      }
    }
  
    return attributes;
  }

  private static List<String> getHeaders(String fileName) {
    // *.map
    if (fileName.equals("Analysis_Sample_meta_info.map")) {
      return ImmutableList.of(
          "EGA_SAMPLE_ID",
          "SAMPLE_ALIAS",
          "BIOSAMPLE_ID");
    } else if (fileName.equals("Study_Experiment_Run_sample.map")) {
      return ImmutableList.of(
          "STUDY_EGA_ID",
          "STUDY_TITLE",
          "STUDY_TYPE",
          "INSTRUMENT_PLATFORM",
          "INSTRUMENT_MODEL",
          "LIBRARY_LAYOUT",
          "LIBRARY_NAME",
          "LIBRARY_STRATEGY",
          "LIBRARY_SOURCE",
          "LIBRARY_SELECTION",
          "EXPERIMENT_EGA_ID",
          "RUN_EGA_ID",
          "SUBMISSION_CENTER_NAME",
          "RUN_CENTER_NAME",
          "EGA_SAMPLE_ID");
    } else if (fileName.equals("Study_analysis_sample.map")) {
      return ImmutableList.of(
          "STUDY EGA_ID",
          "STUDY_TITLE",
          "STUDY_TYPE",
          "ANALYSIS_EGA_ID",
          "ANALYSIS_TYPE",
          "ANALYSIS_TITLE",
          "EGA_SAMPLE_ID",
          "x",
          "y");
    } else if (fileName.equals("Sample_File.map")) {
      return ImmutableList.of(
          "SAMPLE_ALIAS",
          "SAMPLE_ACCESSION",
          "FILE_NAME",
          "FILE_ACCESSION");
    } else if (fileName.equals("Run_Sample.map")) {
      return ImmutableList.of(
          "SAMPLE_ALIAS",
          "SAMPLE_ACCESSION",
          "FILE_NAME",
          "FILE_ACCESSION");
    }

    // *.txt
    if (fileName.equals("Run_Sample.txt") || fileName.equals("Analysis_Sample.txt")) {
      return ImmutableList.of(
          "EGA_SAMPLE_ID",
          "SAMPLE_ALIAS",
          "BIOSAMPLE_ID",
          "SAMPLE_TITLE",
          "ATTRIBUTES");
    } else if (fileName.equals("Study_Experiment_Run_Sample.txt")) {
      return ImmutableList.of(
          "STUDY_EGA_ID",
          "STUDY_TITLE",
          "STUDY_TYPE",
          "INSTRUMENT_PLATFORM",
          "INSTRUMENT_MODEL",
          "LIBRARY_LAYOUT",
          "LIBRARY_NAME",
          "LIBRARY_STRATEGY",
          "LIBRARY_SOURCE",
          "LIBRARY_SELECTION",
          "EXPERIMENT EGA_ID",
          "RUN EGA_ID",
          "SUBMISSION CENTER_NAME",
          "RUN_CENTER_NAME",
          "EGA_SAMPLE_ID",
          "SAMPLE_ALIAS",
          "BIOSAMPLE_ID");
    } else if (fileName.equals("Study_Analysis_Sample.txt")) {
      return ImmutableList.of(
          "STUDY_EGA_ID",
          "STUDY_TITLE",
          "STUDY_TYPE",
          "ANALYSIS_EGA_ID",
          "ANALYSIS_TYPE",
          "ANALYSIS_TITLE",
          "EGA_SAMPLE_ID",
          "SAMPLE_ALIAS",
          "BIOSAMPLE_ID");
    } else if (fileName.equals("Sample_File.txt")) {
      return ImmutableList.of(
          "SAMPLE_ALIAS",
          "SAMPLE_ACCESSION",
          "FILE_NAME",
          "FILE_ACCESSION");
    }

    throw new IllegalArgumentException("Unsupported mapping file name: " + fileName);
  }

}
