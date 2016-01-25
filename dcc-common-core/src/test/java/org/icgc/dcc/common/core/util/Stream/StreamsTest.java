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
package org.icgc.dcc.common.core.util.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.common.core.util.stream.Streams.combineCollections;
import static org.icgc.dcc.common.core.util.stream.Streams.combineCollectionsToSet;
import static org.icgc.dcc.common.core.util.stream.Streams.combineMaps;
import static org.icgc.dcc.common.core.util.stream.Streams.parallelStream;
import static org.icgc.dcc.common.core.util.stream.Streams.toIntegerArray;
import static org.icgc.dcc.common.core.util.stream.Streams.toStringArray;
import static org.junit.rules.ExpectedException.none;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class StreamsTest {

  private static final Class<IllegalStateException> EXCEPTION = IllegalStateException.class;

  /*
   * Test inputs and expected outputs
   */
  private static final Map<String, Integer> map1 = ImmutableMap.of("one", 1);
  private static final Map<String, Integer> map2 = ImmutableMap.of("two", 2);
  private static final Map<String, Integer> map1And2 = ImmutableMap.of("one", 1, "two", 2);

  private static final List<String> list1 = ImmutableList.of("1", "2");
  private static final Set<String> set1 = ImmutableSet.of("3", "4");
  private static final List<String> list2 = ImmutableList.of("5", "6");

  private static final List<String> expectedList = rangeClosed(1, 6)
      .boxed().map(String::valueOf)
      .collect(toImmutableList());
  private static final Set<String> expectedSet = ImmutableSet.copyOf(expectedList);

  private static final Integer[] aMillionElements = toIntegerArray(rangeClosed(1, 1000000)
      .boxed().collect(toImmutableList()));

  @Rule
  public ExpectedException thrown = none();

  @Test
  public void combineMapsTest() {
    sameThing(combineMaps(map1, map2), map1And2);
  }

  @Test
  public void combineMapsOfDuplicateKeysShouldFailTest() {
    thrown.expect(EXCEPTION);
    thrown.expectMessage("Duplicate key");

    combineMaps(map1, map1);
  }

  @Test
  public void combineCollectionsTest() {
    sameThing(combineCollections(list1, set1, list2), expectedList);
  }

  @Test
  public void combineCollectionsToSetTest() {
    sameThing(combineCollectionsToSet(set1, list2, list1, set1, set1, list2, list1), expectedSet);
  }

  @Test
  public void parallelStreamTest() {
    sameThing(parallelStream(newArrayList(aMillionElements)).mapToInt(i -> i).sum(), 1784293664);
  }

  @Test
  public void toStringArrayTest() {
    sameThing(newArrayList(toStringArray(expectedList)), expectedList);
  }

  /*
   * Helpers
   */
  private static <T> void sameThing(T actual, T expected) {
    assertThat(actual).isEqualTo(expected);
  }

}
