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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.model.ClinicalType.CLINICAL_SUPPLEMENTAL_TYPE;
import static org.icgc.dcc.common.core.model.FileTypes.FileSubType.META_SUBTYPE;
import static org.icgc.dcc.common.core.model.FileTypes.FileSubType.PRIMARY_SUBTYPE;
import static org.icgc.dcc.common.core.model.FileTypes.FileSubType.SECONDARY_SUBTYPE;
import static org.icgc.dcc.common.core.util.Strings2.getFirstCharacter;

import java.util.List;
import java.util.Set;

import org.icgc.dcc.common.core.model.DataType.DataTypes;
import org.icgc.dcc.common.core.model.FeatureTypes.FeatureType;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

/**
 * Contains names for file schemata (eg. "ssm_p", "cnsm_s", "exp_g", "N/A", ...)
 */
@NoArgsConstructor(access = PRIVATE)
public final class FileTypes {

  public static final String FILE_EXTENSION = ".txt";

  /**
   * Used as placeholder in the loader for imported fields.
   */
  public static final String NOT_APPLICABLE = "NA";

  /**
   * TODO: migrate all constants below to this enum (DCC-1452).
   * <p>
   * According to https://wiki.oicr.on.ca/display/DCCINT/Submission+File+Format, this would have to be called "FileType"
   * as well, like "donor", "specimen", ... This seems quite confusing however.
   */

  public enum FileSubType implements Identifiable {

    //
    // Clinical
    //

    DONOR_SUBTYPE,
    SPECIMEN_SUBTYPE,
    SAMPLE_SUBTYPE,

    //
    // Clinical Supplemental
    //

    BIOMARKER_SUBTYPE,
    FAMILY_SUBTYPE,
    EXPOSURE_SUBTYPE,
    SURGERY_SUBTYPE,
    THERAPY_SUBTYPE,

    //
    // Feature Types
    //

    META_SUBTYPE,
    PRIMARY_SUBTYPE,
    SECONDARY_SUBTYPE,

    //
    // System
    //

    SYSTEM_SUBTYPE;

    @Override
    public String getId() {
      return usedAsAbbrevatiation() ? getAbbreviation() : getFullName();
    }

    public String getAbbreviation() {
      checkState(usedAsAbbrevatiation(),
          "Clinical sub types do not use abbreviations, attempt was made on %s", this);
      return getFirstCharacter(name()).toLowerCase();
    }

    public String getFullName() {
      checkState(!usedAsAbbrevatiation(),
          "Non-clinical sub types use abbreviations, attempt was made on %s", this);
      return name().replace(DataType.SUBTYPE_SUFFIX, "").toLowerCase();
    }

    public boolean isMetaSubType() {
      return this == META_SUBTYPE;
    }

    public boolean isPrimarySubType() {
      return this == PRIMARY_SUBTYPE;
    }

    public boolean isSecondarySubType() {
      return this == SECONDARY_SUBTYPE;
    }

    public boolean isSystemSubType() {
      return this == SYSTEM_SUBTYPE;
    }

    public boolean isOptionalSubType() {
      return CLINICAL_SUPPLEMENTAL_SUBTYPES.contains(this);
    }

    /**
     * These sub-types are always provided for a submission to be {@link SubmissionState#VALID}.
     */
    public static final Set<FileSubType> MANDATORY_SUBTYPES =
        new ImmutableSet.Builder<FileSubType>()
            .add(DONOR_SUBTYPE)
            .add(SPECIMEN_SUBTYPE)
            .add(SAMPLE_SUBTYPE)
            .build();

    /**
     * These sub-types are optional for a submission to be {@link SubmissionState#VALID}.
     */
    public static final Set<FileSubType> CLINICAL_SUPPLEMENTAL_SUBTYPES =
        new ImmutableSet.Builder<FileSubType>()
            .add(BIOMARKER_SUBTYPE)
            .add(FAMILY_SUBTYPE)
            .add(EXPOSURE_SUBTYPE)
            .add(SURGERY_SUBTYPE)
            .add(THERAPY_SUBTYPE)
            .build();

    public static final Set<FileSubType> CLINICAL_SUBTYPES =
        new ImmutableSet.Builder<FileSubType>()
            .addAll(MANDATORY_SUBTYPES)
            .addAll(CLINICAL_SUPPLEMENTAL_SUBTYPES)
            .build();

    /**
     * See {@link #usedAsAbbrevatiation()}.
     */
    private static final List<FileSubType> TYPES_USED_AS_ABBREVIATION =
        newArrayList(META_SUBTYPE, PRIMARY_SUBTYPE, SECONDARY_SUBTYPE);

    /**
     * Determines whether the sub-type is used as abbreviation for further qualification (for instance "meta" is used as
     * the "_m" suffix) or not (for instance "donor").
     */
    private boolean usedAsAbbrevatiation() {
      return TYPES_USED_AS_ABBREVIATION.contains(this);
    }

    public Set<FileType> getCorrespondingFileTypes() {
      val subType = this;
      return newLinkedHashSet(filter(
          newArrayList(FileType.values()),
          new Predicate<FileType>() {

            @Override
            public boolean apply(FileType fileType) {
              return !fileType.deprecated && fileType.getSubType() == subType;
            }

          }));
    }

  }

  public enum FileType implements Identifiable {

    //
    // Clinical
    //

    DONOR_TYPE(ClinicalType.CLINICAL_CORE_TYPE, FileSubType.DONOR_SUBTYPE),
    SPECIMEN_TYPE(ClinicalType.CLINICAL_CORE_TYPE, FileSubType.SPECIMEN_SUBTYPE),
    SAMPLE_TYPE(ClinicalType.CLINICAL_CORE_TYPE, FileSubType.SAMPLE_SUBTYPE),

    //
    // Clinical Supplemental
    //

    BIOMARKER_TYPE(ClinicalType.CLINICAL_SUPPLEMENTAL_TYPE, FileSubType.BIOMARKER_SUBTYPE),
    FAMILY_TYPE(ClinicalType.CLINICAL_SUPPLEMENTAL_TYPE, FileSubType.FAMILY_SUBTYPE),
    EXPOSURE_TYPE(ClinicalType.CLINICAL_SUPPLEMENTAL_TYPE, FileSubType.EXPOSURE_SUBTYPE),
    SURGERY_TYPE(ClinicalType.CLINICAL_SUPPLEMENTAL_TYPE, FileSubType.SURGERY_SUBTYPE),
    THERAPY_TYPE(ClinicalType.CLINICAL_SUPPLEMENTAL_TYPE, FileSubType.THERAPY_SUBTYPE),

    //
    // Feature Types
    //

    SSM_M_TYPE(FeatureType.SSM_TYPE, FileSubType.META_SUBTYPE),
    SSM_P_TYPE(FeatureType.SSM_TYPE, FileSubType.PRIMARY_SUBTYPE),
    SSM_S_TYPE(FeatureType.SSM_TYPE, FileSubType.SECONDARY_SUBTYPE),

    CNSM_M_TYPE(FeatureType.CNSM_TYPE, FileSubType.META_SUBTYPE),
    CNSM_P_TYPE(FeatureType.CNSM_TYPE, FileSubType.PRIMARY_SUBTYPE),
    CNSM_S_TYPE(FeatureType.CNSM_TYPE, FileSubType.SECONDARY_SUBTYPE),

    STSM_M_TYPE(FeatureType.STSM_TYPE, FileSubType.META_SUBTYPE),
    STSM_P_TYPE(FeatureType.STSM_TYPE, FileSubType.PRIMARY_SUBTYPE),
    STSM_S_TYPE(FeatureType.STSM_TYPE, FileSubType.SECONDARY_SUBTYPE),

    SGV_M_TYPE(FeatureType.SGV_TYPE, FileSubType.META_SUBTYPE),
    SGV_P_TYPE(FeatureType.SGV_TYPE, FileSubType.PRIMARY_SUBTYPE),
    SGV_S_TYPE(FeatureType.SGV_TYPE, FileSubType.SECONDARY_SUBTYPE),

    CNGV_M_TYPE(FeatureType.CNGV_TYPE, FileSubType.META_SUBTYPE),
    CNGV_P_TYPE(FeatureType.CNGV_TYPE, FileSubType.PRIMARY_SUBTYPE),
    CNGV_S_TYPE(FeatureType.CNGV_TYPE, FileSubType.SECONDARY_SUBTYPE),

    STGV_M_TYPE(FeatureType.STGV_TYPE, FileSubType.META_SUBTYPE),
    STGV_P_TYPE(FeatureType.STGV_TYPE, FileSubType.PRIMARY_SUBTYPE),
    STGV_S_TYPE(FeatureType.STGV_TYPE, FileSubType.SECONDARY_SUBTYPE),

    PEXP_M_TYPE(FeatureType.PEXP_TYPE, FileSubType.META_SUBTYPE),
    PEXP_P_TYPE(FeatureType.PEXP_TYPE, FileSubType.PRIMARY_SUBTYPE),

    METH_ARRAY_M_TYPE(FeatureType.METH_ARRAY_TYPE, FileSubType.META_SUBTYPE),
    METH_ARRAY_PROBES_TYPE(FeatureType.METH_ARRAY_TYPE, FileSubType.SYSTEM_SUBTYPE),
    METH_ARRAY_P_TYPE(FeatureType.METH_ARRAY_TYPE, FileSubType.PRIMARY_SUBTYPE),

    METH_SEQ_M_TYPE(FeatureType.METH_SEQ_TYPE, FileSubType.META_SUBTYPE),
    METH_SEQ_P_TYPE(FeatureType.METH_SEQ_TYPE, FileSubType.PRIMARY_SUBTYPE),

    MIRNA_SEQ_M_TYPE(FeatureType.MIRNA_SEQ_TYPE, FileSubType.META_SUBTYPE),
    MIRNA_SEQ_P_TYPE(FeatureType.MIRNA_SEQ_TYPE, FileSubType.PRIMARY_SUBTYPE),

    JCN_M_TYPE(FeatureType.JCN_TYPE, FileSubType.META_SUBTYPE),
    JCN_P_TYPE(FeatureType.JCN_TYPE, FileSubType.PRIMARY_SUBTYPE),

    EXP_ARRAY_M_TYPE(FeatureType.EXP_ARRAY_TYPE, FileSubType.META_SUBTYPE),
    EXP_ARRAY_P_TYPE(FeatureType.EXP_ARRAY_TYPE, FileSubType.PRIMARY_SUBTYPE),

    EXP_SEQ_M_TYPE(FeatureType.EXP_SEQ_TYPE, FileSubType.META_SUBTYPE),
    EXP_SEQ_P_TYPE(FeatureType.EXP_SEQ_TYPE, FileSubType.PRIMARY_SUBTYPE),

    /**
     * Deprecated values
     * @see See https://jira.oicr.on.ca/browse/DCC-4543
     */
    @Deprecated EXP_G(null, null, true),
    @Deprecated EXP_M(null, null, true),
    @Deprecated HSAP_GENE(null, null, true),
    @Deprecated HSAP_TRANSCRIPT(null, null, true),
    @Deprecated METH_M(null, null, true),
    @Deprecated METH_P(null, null, true),
    @Deprecated METH_S(null, null, true),
    @Deprecated MIRNA_M(null, null, true),
    @Deprecated MIRNA_MIRBASE(null, null, true),
    @Deprecated MIRNA_P(null, null, true),
    @Deprecated MIRNA_S(null, null, true);

    /**
     * Constants.
     */
    private static final String PROBES = "probes";
    private static final String TYPE_SUFFIX = "_TYPE";
    private static final Joiner JOINER = Joiner.on("_");

    public static final Set<FileType> MANDATORY_TYPES = newLinkedHashSet(
        filter(
            newLinkedHashSet(newArrayList(FileType.values())),
            new Predicate<FileType>() {

              @Override
              public boolean apply(FileType input) {
                return !input.deprecated && DataTypes.isMandatoryType(input.dataType);
              }

            }));

    private FileType(DataType type) {
      this(type, null, false);
    }

    private FileType(DataType type, FileSubType subType) {
      this(type, subType, false);
    }

    private FileType(DataType dataType, FileSubType subType, boolean deprecated) {
      this.dataType = dataType;
      this.subType = subType;
      this.deprecated = deprecated;
    }

    /**
     * Metadata
     */
    @Getter
    private final DataType dataType;
    @Getter
    private final FileSubType subType;
    @Getter
    private final boolean deprecated;

    @Override
    public String getId() {
      if (subType.usedAsAbbrevatiation()) {
        return JOINER.join(dataType.getId(), subType.getAbbreviation());
      } else if (subType.isSystemSubType()) {
        return JOINER.join(dataType.getId(), PROBES);
      } else {
        return subType.getFullName();
      }
    }

    public boolean isSsmP() {
      return this == SSM_P_TYPE;
    }

    public boolean isSsmS() {
      return this == SSM_S_TYPE;
    }

    public boolean isDonor() {
      return this == DONOR_TYPE;
    }

    public boolean isSpecimen() {
      return this == SPECIMEN_TYPE;
    }

    public boolean isSample() {
      return this == SAMPLE_TYPE;
    }

    public boolean isSgvS() {
      return this == SGV_S_TYPE;
    }

    public boolean isSimpleSecondary() {
      return isSsmS() || isSgvS();
    }

    public boolean isMeta() {
      return getSubType() == META_SUBTYPE;
    }

    public boolean isPrimary() {
      return getSubType() == PRIMARY_SUBTYPE;
    }

    public boolean isSecondary() {
      return getSubType() == SECONDARY_SUBTYPE;
    }

    public boolean isOptional() {
      return getDataType() == CLINICAL_SUPPLEMENTAL_TYPE;
    }

    /**
     * Returns the "harmonized" (uncompressed concatenated) file name.
     * <p>
     * fs-convention
     */
    public String getHarmonizedOutputFileName() {
      return getId() + FILE_EXTENSION;
    }

    /**
     * Returns an enum matching the type like "ssm_p", "cnsm_s", ...
     * <p>
     * TODO: phase out as Strings are replaced with enums.
     */
    public static FileType from(String typeName) {
      return valueOf(typeName.toUpperCase() + TYPE_SUFFIX);
    }

    public static Function<FileType, FileSubType> getGetSubTypeFunction() {

      return new Function<FileType, FileSubType>() {

        @Override
        public FileSubType apply(FileType fileType) {
          return fileType.getSubType();
        }

      };
    }

    public static Iterable<DataType> getDataTypes(@NonNull final Iterable<FileType> fileTypes) {
      return ImmutableSet.copyOf(transform(fileTypes, toDataType()));
    }

    public static Function<FileType, DataType> toDataType() {
      return new Function<FileType, DataType>() {

        @Override
        public DataType apply(FileType fileType) {
          return fileType.getDataType();
        }

      };
    }

    public static final Set<FileType> CLINICAL_SUPPLEMENTAL_FILE_TYPES =
        new ImmutableSet.Builder<FileType>()
            .add(BIOMARKER_TYPE)
            .add(FAMILY_TYPE)
            .add(EXPOSURE_TYPE)
            .add(SURGERY_TYPE)
            .add(THERAPY_TYPE)
            .build();

  }

}
