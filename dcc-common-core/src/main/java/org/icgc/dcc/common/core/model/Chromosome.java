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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.primitives.Ints.tryParse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Represents a Chromosome, with a name and the allowed max position.
 */
@Getter
@RequiredArgsConstructor
public enum Chromosome {

  CHR1("1", 249250621),
  CHR2("2", 243199373),
  CHR3("3", 198022430),
  CHR4("4", 191154276),
  CHR5("5", 180915260),
  CHR6("6", 171115067),
  CHR7("7", 159138663),
  CHR8("8", 146364022),
  CHR9("9", 141213431),
  CHR10("10", 135534747),
  CHR11("11", 135006516),
  CHR12("12", 133851895),
  CHR13("13", 115169878),
  CHR14("14", 107349540),
  CHR15("15", 102531392),
  CHR16("16", 90354753),
  CHR17("17", 81195210),
  CHR18("18", 78077248),
  CHR19("19", 59128983),
  CHR20("20", 63025520),
  CHR21("21", 48129895),
  CHR22("22", 51304566),
  X("X", 155270560),
  Y("Y", 59373566),
  MT("MT", 16569);

  @NonNull
  private final String name;
  /**
   * Max position for a chromosome
   */
  private final int length;

  /*
   * Constants
   */
  private static final String PREFIX = enforcesUppercase("chr");
  private static final int PREFIX_LENGTH = PREFIX.length();

  private static final Map<String, Chromosome> LOOKUP_TABLE_BY_NAME;

  static {
    Chromosome[] values = Chromosome.values();
    val lookup = new HashMap<String, Chromosome>(values.length);

    for (val value : values) {
      lookup.put(enforcesUppercase(value.name), value);
    }

    LOOKUP_TABLE_BY_NAME = Collections.unmodifiableMap(lookup);
  }

  private static String enforcesUppercase(final String input) {
    return input.toUpperCase();
  }

  /**
   * Gets a Chromosome instance by its name (1-22, X, Y or MT).
   */
  public static Chromosome byName(final String name) {
    checkArgument(!isNullOrEmpty(name), "The name of a chromosome must not empty or null.");

    val result = LOOKUP_TABLE_BY_NAME.get(name);

    if (null == result) {
      throw new IllegalArgumentException("Invalid chromosome name (must be 1-22, X, Y or MT): " + name);
    }

    return result;
  }

  /**
   * Gets a Chromosome instance by its literal name in the format of 'chr' plus 1-22 or 'x', 'y', 'mt'. This might
   * appear redundant to the built-in valueOf(); however, this supports literals in lowercase too.
   */
  public static Chromosome byExpression(final String expression) {
    checkArgument(!isNullOrEmpty(expression), "The name of a chromosome must not empty or null.");

    val input = enforcesUppercase(expression.trim());

    // Try the fast way first.
    try {
      return Chromosome.valueOf(input);
    } catch (Exception e) {
      /*
       * This empty catch is intentional so that the function can continue on.
       */
    }

    val name =
        (input.startsWith(PREFIX) && input.length() > PREFIX_LENGTH) ? input.substring(PREFIX_LENGTH) : input;
    return byName(name);
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   * Checks if the position falls within the range of 0 to the max length associated with a particular chromosome. If it
   * does not, an exception will be thrown.
   *
   * @param position
   *
   * @throws IllegalArgumentException if position exceeds the max associated with the chromosome.
   */
  public void checkPosition(final int position) {
    checkArgument(position >= 0, "The 'position' argument must not be negative.");

    checkArgument(position <= this.length, "The requested position (" + position + ") exceeds the max limit ("
        + this.length + ") for Chromosome " + this.name);
  }

  /**
   * Parses a string to a valid length for a chromosome. It will throw an exception when an error occurs.
   */
  public Integer parsePosition(final String position) {
    checkArgument(!isNullOrEmpty(position), "The 'position' argument must not empty or null.");

    val cleaned = position.trim().replaceAll(",", "");
    val parsedPosition = tryParse(cleaned);

    if (null == parsedPosition) {
      throw new IllegalArgumentException("Error parsing '" + position + "' to an integer.");
    }

    checkPosition(parsedPosition);
    return parsedPosition;
  }
}
