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
package org.icgc.dcc.common.ega.core;

import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Curated list of project code to EGA study / dataset mappings.
 * <p>
 * Required since not fully derivable from EGA metadata (yet).
 */
@NoArgsConstructor(access = PRIVATE)
public final class EGAProjectDatasets {

  private static final List<Record> DATASETS = records(
      record("BLCA-CN", "EGAS00001000677", "EGAD00001000758"),
      record("COCA-CN", "EGAS00001001088", null),
      record("COCA-CN", "EGAS00001001200", null),
      record("COCA-CN", "EGAS00001001309", null),
      record("COCA-CN", "EGAS00001001310", null),
      record("ESCA-CN", "EGAS00001000709", "EGAD00001000760"),
      record("ESCA-CN", "EGAS00001001475", null),
      record("ESCA-CN", "EGAS00001001518", null),
      record("GACA-CN", "EGAS00001000675", null),
      record("LUSC-CN", "EGAS00001001087", null),
      record("RECA-CN", "EGAS00001000676", null),
      record("LAML-CN", "EGAS00001001742", null),
      record("LICA-CN", "EGAS00001001660", null),
      record("LIAD-FR", "EGAS00001000679", "EGAD00001000737"),
      record("LICA-FR", "EGAS00001000217", "EGAD00001000131"),
      record("LICA-FR", "EGAS00001000679", "EGAD00001000737"),
      record("LICA-FR", "EGAS00001000706", "EGAD00001000749"),
      record("LICA-FR", "EGAS00001001002", "EGAD00001001096"),
      record("LIHM-FR", "EGAS00001001002", "EGAD00001001096"),
      record("BOCA-FR", "EGAS00001000855", "EGAD00001001051"),
      record("BOCA-UK", "EGAS00001000038", "EGAD00001000358"),
      record("BOCA-UK", "EGAS00001000038", "EGAD00010000432"),
      record("BRCA-UK", "EGAS00001000206", "EGAD00001000133"),
      record("BRCA-UK", "EGAS00001000031", "EGAD00001000138"),
      record("CMDI-UK", "EGAS00001000089", "EGAD00001000045"),
      record("CMDI-UK", "EGAS00001000089", "EGAD00001000117"),
      record("CMDI-UK", "EGAS00001000089", "EGAD00001000283"),
      record("PRAD-UK", "EGAS00001000262", "EGAD00001000263"),
      record("PRAD-UK", "EGAS00001000262", "EGAD00001000689"),
      record("PRAD-UK", "EGAS00001000262", "EGAD00001000891"),
      record("PRAD-UK", "EGAS00001000262", "EGAD00001000892"),
      record("PRAD-UK", "EGAS00001000262", "EGAD00001001116"),
      record("PRAD-UK", "EGAS00001000262", "EGAD00010000498"),
      record("ESAD-UK", "EGAS00001000559", "EGAD00001000704"),
      record("ESAD-UK", "EGAS00001000559", "EGAD00001001071"),
      record("ESAD-UK", "EGAS00001000724", "EGAD00001001048"),
      record("ESAD-UK", "EGAS00001000724", "EGAD00001001067"),
      record("ESAD-UK", "EGAS00001000724", "EGAD00001001071"),
      record("ESAD-UK", "EGAS00001000724", "EGAD00001001394"),
      record("ESAD-UK", "EGAS00001000724", "EGAD00001001457"),
      record("ESAD-UK", "EGAS00001000725", null),
      record("CLLE-ES", "EGAS00000000092", "EGAD00001000023"),
      record("CLLE-ES", "EGAS00000000092", "EGAD00001000044"),
      record("CLLE-ES", "EGAS00000000092", "EGAD00001000083"),
      record("CLLE-ES", "EGAS00000000092", "EGAD00010000238"),
      record("CLLE-ES", "EGAS00000000092", "EGAD00010000280"),
      record("CLLE-ES", "EGAS00000000092", "EGAD00010000470"),
      record("CLLE-ES", "EGAS00001000374", "EGAD00010000472"),
      record("CLLE-ES", null, "EGAD00010000642"),
      record("CLLE-ES", "EGAS00001001306", "EGAD00010000805"),
      record("CLLE-ES", "EGAS00001000374", "EGAD00001000258"),
      record("CLLE-ES", "EGAS00001001306", "EGAD00001001443"),
      record("CLLE-ES", "EGAS00001000272", "EGAD00001000177"),
      record("CLLE-ES", "EGAS00001000272", "EGAD00010000254"),
      record("EOPC-DE", "EGAS00001000400", "EGAD00001000303"),
      record("EOPC-DE", "EGAS00001000400", "EGAD00001000304"),
      record("EOPC-DE", "EGAS00001000400", "EGAD00001000305"),
      record("EOPC-DE", "EGAS00001000400", "EGAD00001000306"),
      record("EOPC-DE", "EGAS00001000400", "EGAD00001000632"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000278"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000279"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000281"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000355"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000356"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000645"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000648"),
      record("MALY-DE", "EGAS00001000394", "EGAD00001000650"),
      record("MALY-DE", "EGAS00001000394", "EGAD00010000377"),
      record("MALY-DE", "EGAS00001000394", "EGAD00010000379"),
      record("MALY-DE", "EGAS00001001067", "EGAD00001001119"),
      record("MALY-DE", "EGAS00001001067", "EGAD00001001120"),
      record("MALY-DE", "EGAS00001001067", "EGAD00001001121"),
      record("PBCA-DE", "EGAS00001000085", "EGAD00001000027"),
      record("PBCA-DE", "EGAS00001000085", "EGAD00001000697"),
      record("PBCA-DE", "EGAS00001000215", "EGAD00001000122"),
      record("PBCA-DE", "EGAS00001000215", "EGAD00001000327"),
      record("PBCA-DE", "EGAS00001000215", "EGAD00001000328"),
      record("PBCA-DE", "EGAS00001000215", "EGAD00001000697"),
      record("PBCA-DE", "EGAS00001000381", "EGAD00001000271"),
      record("PBCA-DE", "EGAS00001000381", "EGAD00001000616"),
      record("PBCA-DE", "EGAS00001000381", "EGAD00001000617"),
      record("PBCA-DE", "EGAS00001000393", "EGAD00001000275"),
      record("PBCA-DE", "EGAS00001000607", "EGAD00001000697"),
      record("PBCA-DE", "EGAS00001000607", "EGAD00001000698"),
      record("PBCA-DE", "EGAS00001000607", "EGAD00001000699"),
      record("PBCA-DE", "EGAS00001000744", "EGAD00001000816"),
      record("PBCA-DE", "EGAS00001000561", "EGAD00001000644"),
      record("PBCA-DE", "EGAS00001000561", "EGAD00010000562"),
      record("LINC-JP", "EGAS00001000389", "EGAD00001000446"),
      record("LINC-JP", "EGAS00001000389", "EGAD00001001024"),
      record("LINC-JP", "EGAS00001000389", "EGAD00001001030"),
      record("LINC-JP", "EGAS00001000389", "EGAD00001001270"),
      record("LINC-JP", "EGAS00001000671", "EGAD00001001262"),
      record("LINC-JP", "EGAS00001000671", "EGAD00001001263"),
      record("LIRI-JP", "EGAS00001000678", "EGAD00001000808"),
      record("BTCA-JP", "EGAS00001000950", "EGAD00001001076"),
      record("ORCA-IN", "EGAS00001000249", "EGAD00001000272"),
      record("ORCA-IN", "EGAS00001001028", "EGAD00001001060"),
      record("MELA-AU", "EGAS00001001552", null),
      record("OV-AU", "EGAS00001000154", "EGAD00001000049"),
      record("OV-AU", "EGAS00001000154", "EGAD00001000096"),
      record("OV-AU", "EGAS00001000154", "EGAD00001000323"),
      record("OV-AU", "EGAS00001000154", "EGAD00001000660"),
      record("OV-AU", "EGAS00001000154", "EGAD00001000371"),
      record("PACA-AU", "EGAS00001000154", "EGAD00001000049"),
      record("PACA-AU", "EGAS00001000154", "EGAD00001000096"),
      record("PACA-AU", "EGAS00001000154", "EGAD00001000323"),
      record("PACA-AU", "EGAS00001000154", "EGAD00001000660"),
      record("PACA-AU", "EGAS00001000154", "EGAD00001000371"),
      record("PAEN-AU", "EGAS00001000154", "EGAD00001000049"),
      record("PAEN-AU", "EGAS00001000154", "EGAD00001000096"),
      record("PAEN-AU", "EGAS00001000154", "EGAD00001000323"),
      record("PAEN-AU", "EGAS00001000154", "EGAD00001000660"),
      record("PAEN-AU", "EGAS00001000154", "EGAD00001000371"),
      record("PACA-CA", "EGAS00001000395", "EGAD00001001595"),
      record("PACA-CA", "EGAS00001000395", "EGAD00001001095"),
      record("PACA-CA", "EGAS00001000395", "EGAD00001001956"),
      record("PRAD-CA", "EGAS00001000900", "EGAD00001001094"),
      record("RECA-EU", "EGAS00001000083", "EGAD00001000718"),
      record("RECA-EU", "EGAS00001000083", "EGAD00001000719"),
      record("RECA-EU", "EGAS00001000083", "EGAD00001000709"),
      record("RECA-EU", "EGAS00001000083", "EGAD00001000717"),
      record("RECA-EU", "EGAS00001000083", "EGAD00001000720"),
      record("PAEN-IT", "EGAS00001000154", "EGAD00001000049"),
      record("PAEN-IT", "EGAS00001000154", "EGAD00001000096"),
      record("PAEN-IT", "EGAS00001000154", "EGAD00001000323"),
      record("PAEN-IT", "EGAS00001000154", "EGAD00001000660"),
      record("PAEN-IT", "EGAS00001000154", "EGAD00001000371"),
      record("THCA-SA", "EGAS00001000680", null),
      record("SKCA-BR", "EGAS00001001052", null),
      record("LAML-KR", "EGAS00001001082", null),
      record("LAML-KR", "EGAS00001001559", null),
      record("LUSC-KR", "EGAS00001001083", null),
      record("LUSC-KR", "EGAS00001001474", null),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001322"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001323"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001334"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001335"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001336"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001337"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001338"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001339"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001340"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001341"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00001001388"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00010000915"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00010000916"),
      record("BRCA-EU", "EGAS00001001178", "EGAD00010000917"));

  @Value
  private static class Record {

    String projectCode;
    String studyId;
    String datasetId;

  }

  public static List<String> getStudyProjectCodes(String studyId) {
    return DATASETS.stream()
        .filter(r -> studyId.equals(r.getStudyId()))
        .map(Record::getProjectCode)
        .collect(toImmutableList());
  }

  public static List<String> getDatasetProjectCodes(String datasetId) {
    return DATASETS.stream()
        .filter(r -> datasetId.equals(r.getDatasetId()))
        .map(Record::getProjectCode)
        .collect(toImmutableList());
  }

  private static Record record(String projectCode, String studyId, String datasetId) {
    return new Record(projectCode, studyId, datasetId);
  }

  private static List<Record> records(Record... records) {
    return ImmutableList.<Record> builder().add(records).build();
  }

}
