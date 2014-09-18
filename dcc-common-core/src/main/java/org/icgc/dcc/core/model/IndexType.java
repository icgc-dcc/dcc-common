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
package org.icgc.dcc.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.FieldDefaults;

/**
 * ElasticSearch index types.
 */
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public enum IndexType {

  /**
   * Release type(s).
   */
  RELEASE_TYPE(Entity.PROJECT, "release", Classifier.BASIC),

  /**
   * Project type(s).
   */
  PROJECT_TYPE(Entity.PROJECT, "project", Classifier.BASIC),
  PROJECT_TEXT_TYPE(Entity.PROJECT, "project-text", Classifier.BASIC),

  /**
   * Donor type(s).
   */
  DONOR_TYPE(Entity.DONOR, "donor", Classifier.BASIC),
  DONOR_TEXT_TYPE(Entity.DONOR, "donor-text", Classifier.BASIC),
  DONOR_CENTRIC_TYPE(Entity.DONOR, "donor-centric", Classifier.BASIC),

  /**
   * Gene type(s).
   */
  GENE_TYPE(Entity.GENE, "gene", Classifier.BASIC),
  GENE_TEXT_TYPE(Entity.GENE, "gene-text", Classifier.BASIC),
  GENE_CENTRIC_TYPE(Entity.GENE, "gene-centric", Classifier.CENTRIC),

  /**
   * Observation type(s).
   */
  OBSERVATION_CENTRIC_TYPE(Entity.OBSERVATION, "observation-centric", Classifier.CENTRIC),

  /**
   * Mutation type(s).
   */
  MUTATION_TEXT_TYPE(Entity.MUTATION, "mutation-text", Classifier.CENTRIC),
  MUTATION_CENTRIC_TYPE(Entity.OBSERVATION, "mutation-centric", Classifier.CENTRIC),

  /**
   * Pathway types
   */
  PATHWAY_TYPE(Entity.PATHWAY, "pathway", Classifier.BASIC),
  PATHWAY_TEXT_TYPE(Entity.PATHWAY, "pathway-text", Classifier.BASIC);

  /**
   * The corresponding entity of the index type.
   */
  Entity entity;

  /**
   * The name of the index type.
   */
  String name;

  /**
   * The classifier of the index type.
   */
  Classifier classifier;

  public static IndexType byName(String name) {
    checkArgument(name != null, "Target name for class '%s' cannot be null", IndexType.class.getName());

    for (val value : values()) {
      if (name.equals(value.name)) {
        return value;
      }
    }

    throw new IllegalArgumentException("No " + IndexType.class.getName() + " value with name '" + name + "' found");
  }

  @Override
  public String toString() {
    return name;
  }

  public enum Classifier {
    BASIC,
    CENTRIC
  }

}
