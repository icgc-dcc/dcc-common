/*
 * Copyright 2012(c) The Ontario Institute for Cancer Research. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the GNU Public
 * License v3.0. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.common.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;

import lombok.Value;
import lombok.val;

@Value
public class ChromosomeLocation implements Serializable {

  Chromosome chromosome;
  Integer start;
  Integer end;

  public boolean hasStart() {
    return start != null;
  }

  public boolean hasEnd() {
    return end != null;
  }

  private static final String CHROMOSOME_SEPARATOR = ":";
  private static final String RANGE_SEPARATOR = "-";

  private static Integer parsePosition(final String position, final Chromosome chromosome, final int defaultValue) {
    return isNullOrEmpty(position) ? defaultValue : chromosome.parsePosition(position);
  }

  /**
   * Parses a string to a ChromosomeLocation instance, validating the chromosome and its lower and upper bounds along
   * the way. An example of a valid string is: chr1:20-100
   */
  public static ChromosomeLocation parse(final String chromosomeWithRange) {
    checkArgument(!isNullOrEmpty(chromosomeWithRange), "The 'chromosomeWithRange' argument must not empty or null.");

    final String[] parts = chromosomeWithRange.split(CHROMOSOME_SEPARATOR);
    val chromosome = Chromosome.byExpression(parts[0]);
    Integer start = null;
    Integer end = null;

    if (parts.length > 1 && !isNullOrEmpty(parts[1])) {
      final String[] range = parts[1].split(RANGE_SEPARATOR);

      val defaultLowerbound = 0;
      start = parsePosition(range[0], chromosome, defaultLowerbound);

      if (range.length > 1) {
        val defaultUpperbound = chromosome.getLength();
        end = parsePosition(range[1], chromosome, defaultUpperbound);
      }
    }

    return new ChromosomeLocation(chromosome, start, end);
  }
}
