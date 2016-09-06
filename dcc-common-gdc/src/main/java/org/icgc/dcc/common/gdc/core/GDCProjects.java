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

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public final class GDCProjects {

  /**
   * @see https://wiki.oicr.on.ca/pages/viewpage.action?pageId=66946440#
   * ICGCrepositorymetadataJSONmodelupdatetoaccommodateEGA/GDCintegration-ProjectMapping
   */
  private static final Map<String, String> PROJECTS = ImmutableMap.<String, String> builder()
      // TARGET
      .put("TARGET-ALL-P1", "ALL-US")
      .put("TARGET-ALL-P2", "ALL-US")
      .put("TARGET-AML", "AML-US")
      .put("TARGET-CCSK", "CCSK-US")
      .put("TARGET-NBL", "NBL-US")
      .put("TARGET-RT", "RT-US")
      .put("TARGET-WT", "WT-US")

      // TCGA
      .put("TCGA-BLCA", "BLCA-US")
      .put("TCGA-BRCA", "BRCA-US")
      .put("TCGA-CESC", "CESC-US")
      .put("TCGA-COAD", "COAD-US")
      .put("TCGA-DLBC", "DLBC-US")
      .put("TCGA-GBM", "GBM-US")
      .put("TCGA-HNSC", "HNSC-US")
      .put("TCGA-KICH", "KICH-US")
      .put("TCGA-KIRC", "KIRC-US")
      .put("TCGA-KIRP", "KIRP-US")
      .put("TCGA-LAML", "LAML-US")
      .put("TCGA-LGG", "LGG-US")
      .put("TCGA-LIHC", "LIHC-US")
      .put("TCGA-LUAD", "LUAD-US")
      .put("TCGA-LUSC", "LUSC-US")
      .put("TCGA-OV", "OV-US")
      .put("TCGA-PAAD", "PAAD-US")
      .put("TCGA-PRAD", "PRAD-US")
      .put("TCGA-READ", "READ-US")
      .put("TCGA-SARC", "SARC-US")
      .put("TCGA-SKCM", "SKCM-US")
      .put("TCGA-STAD", "STAD-US")
      .put("TCGA-THCA", "THCA-US")
      .put("TCGA-UCEC", "UCEC-US")

      .build();

  public static Set<String> getProjectsIds() {
    return PROJECTS.keySet();
  }

  public static String getProjectCode(@NonNull String projectId) {
    return PROJECTS.get(projectId);
  }

}
