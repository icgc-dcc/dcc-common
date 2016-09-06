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
package org.icgc.dcc.common.gdc.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public final class GDCFiles {

  public static String getAccess(@NonNull ObjectNode file) {
    return file.get("access").textValue();
  }

  public static JsonNode getAnalysis(@NonNull ObjectNode file) {
    return file.path("analysis");
  }

  public static String getAnalysisId(@NonNull ObjectNode file) {
    return getAnalysis(file).path("analysis_id").textValue();
  }

  public static String getAnalysisWorkflowType(@NonNull ObjectNode file) {
    return getAnalysis(file).path("workflow_type").textValue();
  }

  public static String getExperimentalStrategy(@NonNull ObjectNode file) {
    return file.path("experimental_strategy").textValue();
  }

  public static String getDataType(@NonNull ObjectNode file) {
    return file.path("data_type").textValue();
  }

  public static String getDataCategory(@NonNull ObjectNode file) {
    return file.path("data_category").textValue();
  }

  public static String getDataFormat(@NonNull ObjectNode file) {
    return file.get("data_format").textValue();
  }

  public static String getFileId(@NonNull ObjectNode file) {
    return file.get("file_id").textValue();
  }

  public static String getFileName(@NonNull ObjectNode file) {
    return file.get("file_name").textValue();
  }

  public static long getFileSize(@NonNull ObjectNode file) {
    return file.get("file_size").longValue();
  }

  public static String getMd5sum(@NonNull ObjectNode file) {
    return file.get("md5sum").textValue();
  }

  public static String getUpdatedDatetime(@NonNull ObjectNode file) {
    return file.get("updated_datetime").textValue();
  }

  public static JsonNode getIndexFiles(@NonNull ObjectNode file) {
    return file.path("index_files");
  }

  public static String getIndexFileId(@NonNull JsonNode indexFile) {
    return indexFile.get("file_id").textValue();
  }

  public static String getIndexMd5sum(@NonNull JsonNode indexFile) {
    return indexFile.get("md5sum").textValue();
  }

  public static long getIndexFileSize(@NonNull JsonNode indexFile) {
    return indexFile.get("file_size").longValue();
  }

  public static String getIndexDataFormat(@NonNull JsonNode indexFile) {
    return indexFile.get("data_format").textValue();
  }

  public static String getIndexFileName(@NonNull JsonNode indexFile) {
    return indexFile.get("file_name").textValue();
  }

  public static JsonNode getCases(@NonNull ObjectNode file) {
    return file.path("cases");
  }

  public static String getCaseId(@NonNull JsonNode caze) {
    return caze.path("case_id").textValue();
  }

  public static JsonNode getCaseProject(@NonNull JsonNode caze) {
    return caze.path("project");
  }

  public static String getCaseProjectId(@NonNull JsonNode caze) {
    return getCaseProject(caze).path("project_id").textValue();
  }

  public static String getCaseProjectPrimarySite(@NonNull JsonNode caze) {
    return getCaseProject(caze).path("primary_site").textValue();
  }

  public static String getCaseProjectName(@NonNull JsonNode caze) {
    return getCaseProject(caze).path("name").textValue();
  }

  public static JsonNode getCaseSamples(@NonNull JsonNode caze) {
    return caze.path("samples");
  }

  public static String getCaseSampleId(@NonNull JsonNode caseSample) {
    return caseSample.path("sample_id").textValue();
  }

  public static String getCaseSampleType(@NonNull JsonNode caseSample) {
    return caseSample.path("sample_type").textValue();
  }

  public static String getSampleSubmitterId(@NonNull JsonNode sample) {
    return sample.get("submitter_id").textValue();
  }

  public static JsonNode getSamplePortions(@NonNull JsonNode sample) {
    return sample.path("portions");
  }

  public static JsonNode getPortionAnalytes(@NonNull JsonNode portion) {
    return portion.path("analytes");
  }

  public static JsonNode getAnalyteAliquots(@NonNull JsonNode analyte) {
    return analyte.path("aliquots");
  }

  public static String getAliquotId(@NonNull JsonNode aliquot) {
    return aliquot.path("aliquot_id").textValue();
  }

  public static String getAliquotSubmitterId(@NonNull JsonNode aliquot) {
    return aliquot.path("submitter_id").textValue();
  }

}
