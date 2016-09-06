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
package org.icgc.dcc.common.ega.reader;

import static com.google.common.base.Preconditions.checkState;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.icgc.dcc.common.core.util.Splitters.TAB;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.icgc.dcc.common.core.io.ForwardingInputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

public class EGAMappingReader {

  @SneakyThrows
  public List<ObjectNode> read(@NonNull String mappingId, @NonNull InputStream inputStream) {
    val lines = readLines(inputStream);

    try {
      return lines.map(TAB::splitToList)
          .map(toObjectNode(getHeaders(mappingId)))
          .collect(toImmutableList());
    } catch (Exception e) {
      throw new IllegalStateException("Error processing " + mappingId, e);
    }
  }

  @SuppressWarnings("resource")
  private static Stream<String> readLines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(new ForwardingInputStream(inputStream, false))).lines();
  }

  private static Function<List<String>, ObjectNode> toObjectNode(List<String> headers) {
    return fields -> {
      ObjectNode record = DEFAULT.createObjectNode();
      checkState(headers.size() == fields.size(), "Header size (%s) not equal to fields size (%s) for mapping",
          headers.size(), fields.size());

      for (int i = 0; i < headers.size(); i++) {
        record.put(headers.get(i), fields.get(i));
      }

      return record;
    };
  }

  private static List<String> getHeaders(String mappingId) {
    if (mappingId.equals("Run_Sample_meta_info")) {
      return ImmutableList.of(
          "EGA_SAMPLE_ID",
          "SAMPLE_ALIAS",
          "BIOSAMPLE_ID",
          "SAMPLE_TITLE",
          "ATTRIBUTES");
    } else if (mappingId.equals("Analysis_Sample_meta_info")) {
      return ImmutableList.of(
          "EGA_SAMPLE_ID",
          "SAMPLE_ALIAS",
          "BIOSAMPLE_ID");
    } else if (mappingId.equals("Study_Experiment_Run_sample")) {
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
    } else if (mappingId.equals("Study_analysis_sample")) {
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
    } else if (mappingId.equals("Sample_File")) {
      return ImmutableList.of(
          "SAMPLE_ALIAS",
          "SAMPLE_ACCESSION",
          "FILE_NAME",
          "FILE_ACCESSION");
    }

    throw new IllegalArgumentException("Unsupported mapping id: " + mappingId);
  }

}
