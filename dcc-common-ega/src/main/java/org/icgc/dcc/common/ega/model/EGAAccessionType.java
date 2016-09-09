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
// @formatter:off
package org.icgc.dcc.common.ega.model;

import java.util.Optional;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Accession types supported by EGA.
 *
 * @see http://www.ebi.ac.uk/ena/submit/read-data-format#accession_number_format
 * @see http://www.ebi.ac.uk/ena/submit/metadata-model
 */
@Getter
@RequiredArgsConstructor
public enum EGAAccessionType {

  SUBMISSION("EGA Submission", "EGA",  11),
  SAMPLE    ("EGA Sample",     "EGAN", 11),
  STUDY     ("EGA Study",      "EGAS", 11),
  EXPERIMENT("EGA Experiment", "EGAX", 11),
  RUN       ("EGA Run",        "EGAR", 11),
  ANALYSIS  ("EGA Analysis",   "EGAZ", 11),
  DAC       ("EGA DAC",        "EGAC", 11),
  POLICY    ("EGA Policy",     "EGAC", 11),
  DATASET   ("EGA Data Set",   "EGAD", 11),
  FILE      ("EGA File",       "EGAF", 11);

  /**
   * The object type to which the accession type applies.
   */
  private final String metadataObject;

  /**
   * The prefix of an ID.
   * <p>
   * e.g. {@code EGAF00000000001}
   */
  private final String prefix;

  /**
   * The number of digits formatted.
   * <p>
   * e.g. For {@code EGAF00000000001} it would be 11
   */
  private final int digits;

  public boolean isFile() {
    return this == FILE;
  }
  
  public boolean matches(String accession) {
    return Pattern.matches(prefix + "\\d{" + digits + "}", accession);
  }
  
  public static Optional<EGAAccessionType> from(@NonNull String accession) {
    for (EGAAccessionType value : values()) {
      val match = value.matches(accession);
      if (match) return Optional.of(value);
    }

    return Optional.empty();
  }

}
