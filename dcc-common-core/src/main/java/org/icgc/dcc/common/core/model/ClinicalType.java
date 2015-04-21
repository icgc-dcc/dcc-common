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

import static com.google.common.base.Preconditions.checkState;
import static org.icgc.dcc.common.core.model.FileTypes.FileType.DONOR_TYPE;
import static org.icgc.dcc.common.core.util.Joiners.UNDERSCORE;
import static org.icgc.dcc.common.core.util.Strings2.EMPTY_STRING;
import lombok.Getter;
import lombok.NonNull;

import org.icgc.dcc.common.core.model.FeatureTypes.FeatureType;
import org.icgc.dcc.common.core.model.FileTypes.FileSubType;
import org.icgc.dcc.common.core.model.FileTypes.FileType;

/**
 * Represents a (the only one for now) type of clinical data, see {@link FeatureType} for the observation counterpart.
 * <p>
 * The "donor" name is reused here (which makes things a bit confusing...).
 */
public enum ClinicalType implements DataType, Identifiable {

  CLINICAL_CORE_TYPE(FileSubType.DONOR_SUBTYPE.getFullName(), SummaryType.EXISTS),
  CLINICAL_SUPPLEMENTAL_TYPE(CLINICAL_SUPPLEMENTAL_TYPE_NAME, SummaryType.EXISTS);

  private ClinicalType(@NonNull final String id, @NonNull final SummaryType summaryType) {
    this.id = id;
    this.summaryType = summaryType;
  }

  @Getter
  private final String id;

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
    return DONOR_TYPE;
  }

  public boolean isCoreClinicalType() {
    return this == CLINICAL_CORE_TYPE;
  }

  public boolean isSupplementalClinicalType() {
    return this == CLINICAL_SUPPLEMENTAL_TYPE;
  }

  @Override
  public FeatureType asFeatureType() {
    checkState(false, "Not a '%s': '%s'",
        FeatureType.class.getSimpleName(), this);
    return null;
  }

  /**
   * Returns an enum matching the type name provided.
   */
  public static DataType from(String typeName) {
    if (typeName.equals(CLINICAL_CORE_TYPE.getId())) {
      return CLINICAL_CORE_TYPE;
    }
    if (typeName.equals(CLINICAL_SUPPLEMENTAL_TYPE.getId())) {
      return CLINICAL_SUPPLEMENTAL_TYPE;
    }

    throw new IllegalArgumentException(
        "Unknown " + ClinicalType.class.getSimpleName() + "  for type name'" + typeName + "'");
  }

  public String getSummaryFieldName() {

    return UNDERSCORE.join(EMPTY_STRING, this.getId(), summaryType.getId());
  }

  public boolean isCountSummary() {
    // TODO is it?
    return false;
  }

}
