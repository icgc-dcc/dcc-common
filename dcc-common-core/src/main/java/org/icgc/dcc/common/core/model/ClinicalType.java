/*
 * Copyright (c) 2013 The Ontario Institute for Cancer Research. All rights reserved.                             
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.icgc.dcc.common.core.model.FileTypes.FileSubType.CLINICAL_SUPPLEMENTAL_SUBTYPE;
import static org.icgc.dcc.common.core.util.Joiners.UNDERSCORE;
import static org.icgc.dcc.common.core.util.Strings2.EMPTY_STRING;
import static org.icgc.dcc.common.core.util.Strings2.removeTarget;

import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import org.icgc.dcc.common.core.model.FeatureTypes.FeatureType;
import org.icgc.dcc.common.core.model.FileTypes.FileSubType;
import org.icgc.dcc.common.core.model.FileTypes.FileType;

import com.google.common.base.Predicate;

/**
 * Represents a (the only one for now) type of clinical data, see {@link FeatureType} for the observation counterpart.
 * <p>
 * The "donor" name is reused here (which makes things a bit confusing...).
 */
public enum ClinicalType implements DataType {

  DONOR_TYPE(SummaryType.EXISTS),
  SPECIMEN_TYPE(SummaryType.EXISTS),
  SAMPLE_TYPE(SummaryType.EXISTS),
  BIOMARKER_TYPE(SummaryType.EXISTS),
  FAMILY_TYPE(SummaryType.EXISTS),
  EXPOSURE_TYPE(SummaryType.EXISTS),
  SURGERY_TYPE(SummaryType.EXISTS),
  THERAPY_TYPE(SummaryType.EXISTS);

  private ClinicalType(@NonNull final SummaryType summaryType) {
    this.summaryType = summaryType;
  }

  @Getter
  private final SummaryType summaryType;

  @Override
  public boolean isClinicalType() {
    return true;
  }

  @Override
  public boolean isFeatureType() {
    return false;
  }

  @Override
  public ClinicalType asClinicalType() {
    return this;
  }

  @Override
  public FileType getTopLevelFileType() {
    return FileType.DONOR_TYPE;
  }

  // public boolean isCoreClinicalType() {
  // return this == CLINICAL_CORE_TYPE;
  // }

  @Override
  public FeatureType asFeatureType() {
    checkState(false, "Not a '%s': '%s'",
        FeatureType.class.getSimpleName(), this);
    return null;
  }

  /**
   * Returns an enum matching the type name provided.
   */
  public static ClinicalType from(String typeName) {
    return valueOf(typeName.toUpperCase() + TYPE_SUFFIX);
  }

  // public static DataType from(String typeName) {
  // if (typeName.equals(CLINICAL_CORE_TYPE.getId())) {
  // return CLINICAL_CORE_TYPE;
  // }
  // if (typeName.equals(CLINICAL_SUPPLEMENTAL_TYPE.getId())) {
  // return CLINICAL_SUPPLEMENTAL_TYPE;
  // }
  //
  // throw new IllegalArgumentException(
  // "Unknown " + ClinicalType.class.getSimpleName() + "  for type name'" + typeName + "'");
  // }

  public String getSummaryFieldName() {

    return UNDERSCORE.join(EMPTY_STRING, this.getId(), summaryType.getId());
  }

  public boolean isCountSummary() {
    // TODO is it?
    return false;
  }

  /**
   * Returns the file types corresponding to the feature type.
   * <p>
   * TODO: move to {@link FileTypes} rather
   */
  public Set<FileType> getCorrespondingFileTypes() {
    val dataType = this;
    return newLinkedHashSet(filter(
        newArrayList(FileType.values()),
        new Predicate<FileType>() {

          @Override
          public boolean apply(FileType fileType) {
            return fileType.getDataType() == dataType;
          }

        }));
  }

  /**
   * Returns the file type whose presence indicates that the type is considered as "present" and therefore to be
   * processed.
   * <p>
   * TODO: move to {@link FileTypes} rather
   */
  public FileType getDataTypePresenceIndicator() {
    return checkNotNull(
        getSupplementalFileType(),
        "There should be at least one file type that acts as type presence flagship for the feature type '%s'", this);
  }

  public FileType getSupplementalFileType() {
    return getFileType(CLINICAL_SUPPLEMENTAL_SUBTYPE);
  }

  private FileType getFileType(final FileSubType fileSubType) {
    return find( // MUST have a match (by design)
        getCorrespondingFileTypes(),
        new Predicate<FileType>() {

          @Override
          public boolean apply(FileType fileType) {
            return fileType.getSubType() == fileSubType;
          }

        });
  }

  @Override
  public String getId() {
    return removeTarget(name(), TYPE_SUFFIX).toLowerCase();
  }

}
