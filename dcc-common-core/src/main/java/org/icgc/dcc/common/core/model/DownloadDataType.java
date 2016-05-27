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
package org.icgc.dcc.common.core.model;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.CNSM_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.CNSM_FIRST_LEVEL_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.DONOR_EXPOSURE_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.DONOR_FAMILY_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.DONOR_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.DONOR_THERAPY_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.EXP_ARRAY_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.EXP_SEQ_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.JCN_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.METH_ARRAY_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.METH_SEQ_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.MIRNA_SEQ_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.PEXP_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SAMPLE_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SGV_CONTROLLED_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SGV_FIRST_LEVEL_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SPECIMEN_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SSM_CONTROLLED_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SSM_FIRST_LEVEL_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.SSM_SECOND_LEVEL_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.STSM_FIELDS;
import static org.icgc.dcc.common.core.model.DownloadDataTypeFields.STSM_FIRST_LEVEL_FIELDS;
import static org.icgc.dcc.common.core.util.Separators.EMPTY_STRING;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableMap;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableSet;
import static org.icgc.dcc.common.core.util.stream.Streams.stream;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Data type requested by the portal users.
 */
@Getter
public enum DownloadDataType implements Identifiable {

  // The order of fields is important as it will be used in the output file.

  DONOR(DONOR_FIELDS),
  DONOR_FAMILY(DONOR_FAMILY_FIELDS),
  DONOR_THERAPY(DONOR_THERAPY_FIELDS),
  DONOR_EXPOSURE(DONOR_EXPOSURE_FIELDS),
  SPECIMEN(SPECIMEN_FIELDS),
  SAMPLE(SAMPLE_FIELDS),

  CNSM(CNSM_FIELDS, CNSM_FIRST_LEVEL_FIELDS),
  JCN(JCN_FIELDS),
  METH_SEQ(METH_SEQ_FIELDS),
  METH_ARRAY(METH_ARRAY_FIELDS),
  MIRNA_SEQ(MIRNA_SEQ_FIELDS),
  STSM(STSM_FIELDS, STSM_FIRST_LEVEL_FIELDS),
  PEXP(PEXP_FIELDS),
  EXP_SEQ(EXP_SEQ_FIELDS),
  EXP_ARRAY(EXP_ARRAY_FIELDS),

  SSM_OPEN(getSsmOpenFields(), SSM_FIRST_LEVEL_FIELDS, getSsmOpenSecondLevelFields()),
  SSM_CONTROLLED(SSM_CONTROLLED_FIELDS, SSM_FIRST_LEVEL_FIELDS, SSM_SECOND_LEVEL_FIELDS),
  SGV_CONTROLLED(SGV_CONTROLLED_FIELDS, SGV_FIRST_LEVEL_FIELDS);

  private static final String CONTROLLED_SUFFIX = "_controlled";
  private static final String OPEN_SUFFIX = "_open";

  public static final Set<DownloadDataType> CLINICAL = ImmutableSet.of(DONOR, DONOR_FAMILY, DONOR_THERAPY,
      DONOR_EXPOSURE, SPECIMEN, SAMPLE);

  private static final Set<DownloadDataType> CLINICAL_SUBTYPES = CLINICAL.stream()
      .filter(dt -> dt != DONOR)
      .collect(toImmutableSet());

  /**
   * A mapping between field names of output archives consumed by the portal users and field names produced by the ETL
   * ExportJob.<br>
   * <b>NB:</b> The fields must be ordered in the order of the output file.
   */
  private final Map<String, String> fields;

  /**
   * Rows in the parquet files have a nested structure. The processing logic 'unwinds' nested fields to make the row
   * flat. Field levels represent which fields should be used first for projection.<br>
   * E.g. {@code firstLevelFields} are non-nested fields. {@code secondLevelFields} are first nested fields
   */
  private final List<String> firstLevelFields;
  private final List<String> secondLevelFields;

  private DownloadDataType() {
    this(emptyMap());
  }

  private DownloadDataType(@NonNull Map<String, String> fields) {
    this(fields, emptyList(), emptyList());
  }

  private DownloadDataType(@NonNull Map<String, String> fields, List<String> firstLevelFields) {
    this(fields, firstLevelFields, emptyList());
  }

  private DownloadDataType(@NonNull Map<String, String> fields, List<String> firstLevelFields,
      List<String> secondLevelFields) {
    this.fields = fields;
    this.firstLevelFields = firstLevelFields;
    this.secondLevelFields = secondLevelFields;
  }

  @Override
  public String getId() {
    return name().toLowerCase();
  }

  public String getCanonicalName() {
    String name = getId();
    if (isControlled()) {
      name = name.replace(CONTROLLED_SUFFIX, EMPTY_STRING);
    } else if (isOpen()) {
      name = name.replace(OPEN_SUFFIX, EMPTY_STRING);
    }

    return name;
  }

  public boolean isControlled() {
    return getId().endsWith(CONTROLLED_SUFFIX);
  }

  public boolean isOpen() {
    return getId().endsWith(OPEN_SUFFIX);
  }

  public List<String> getDownloadFields() {
    return this.getFields().entrySet().stream()
        .map(e -> e.getKey())
        .collect(toImmutableList());
  }

  public boolean isClinicalSubtype() {
    return CLINICAL_SUBTYPES.contains(this);
  }

  public static boolean hasClinicalDataTypes(@NonNull Set<DownloadDataType> dataTypes) {
    return Sets.intersection(CLINICAL, dataTypes)
        .isEmpty() == false;
  }

  public static DownloadDataType from(@NonNull String name, boolean controlled) {
    val dataTypes = stream(values())
        .filter(dt -> dt.getCanonicalName().equals(name) && dt.isControlled() == controlled)
        .collect(toImmutableList());
    checkState(dataTypes.size() == 1, "Failed to resolve DownloadDataType from name '%s' and controlled '%s'. "
        + "Found data types: %s", name, controlled, dataTypes);

    return dataTypes.get(0);
  }

  /**
   * Resolve {@code DownloadDataType} from the canonical name. If open and controlled version exists returns the
   * controlled one.
   */
  public static DownloadDataType fromCanonical(@NonNull String name) {
    val dataTypes = stream(values())
        .filter(dt -> dt.getCanonicalName().equals(name.toLowerCase()))
        .filter(dt -> dt.isControlled() || !dt.isOpen())
        .collect(toImmutableList());
    checkState(dataTypes.size() == 1, "Failed to resolve DownloadDataType from name '%s'. Found data types: %s", name,
        dataTypes);

    return dataTypes.get(0);
  }

  public static boolean canCreateFrom(@NonNull String name) {
    return stream(values())
        .anyMatch(dt -> dt.name().equals(name));
  }

  /**
   * Returns {@code controlled} version for this data type if it exists otherwise returns the argument.
   */
  public static DownloadDataType toControlledIfPossible(String name) {
    val controlled = stream(values())
        .filter(dt -> dt.getCanonicalName().equals(name.toLowerCase()))
        .filter(dt -> dt.isControlled() || !dt.isOpen())
        .collect(toImmutableList());
    checkState(controlled.size() == 1, "Failed to resolve controlled from %s", name);

    return controlled.get(0);
  }

  private static Map<String, String> getSsmOpenFields() {
    return SSM_CONTROLLED_FIELDS.entrySet().stream()
        .filter(e -> !DownloadDataTypeFields.SSM_CONTROLLED_REMOVE_FIELDS.contains(e.getKey()))
        .collect(toImmutableMap(e -> e.getKey(), e -> e.getValue()));
  }

  private static List<String> getSsmOpenSecondLevelFields() {
    return SSM_SECOND_LEVEL_FIELDS.stream()
        .filter(e -> !DownloadDataTypeFields.SSM_CONTROLLED_REMOVE_FIELDS.contains(e))
        .collect(toImmutableList());
  }

}
