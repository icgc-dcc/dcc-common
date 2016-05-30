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
package org.icgc.dcc.common.core.util.stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
public final class Streams {

  public static <T> Stream<T> stream(@NonNull Iterator<T> iterator) {
    return stream(() -> iterator, false);
  }

  public static <T> Stream<T> stream(@NonNull Iterable<T> iterable) {
    return stream(iterable, false);
  }

  @SafeVarargs
  public static <T> Stream<T> stream(@NonNull T... values) {
    return ImmutableList.copyOf(values).stream();
  }

  public static <T, R> Function<T, Stream<? extends R>> stream(
      @NonNull Function<? super T, ? extends Iterable<? extends R>> mapper) {
    return x -> stream(mapper.apply(x));
  }

  public static <T> Stream<T> parallelStream(@NonNull Iterable<T> iterable) {
    return stream(iterable, true);
  }

  public static <T> T[] toArray(@NonNull Iterable<T> iterable, @NonNull IntFunction<T[]> generator) {
    return stream(iterable).toArray(generator);
  }

  public static String[] toStringArray(@NonNull Iterable<String> strings) {
    return toArray(strings, String[]::new);
  }

  public static Integer[] toIntegerArray(@NonNull Iterable<Integer> integers) {
    return toArray(integers, Integer[]::new);
  }

  /**
   * Caller must guarantee the uniqueness of keys across all maps; otherwise must expect to handle "Duplicate key"
   * IllegalStateException.
   */
  public static <K, V> Map<K, V> combineMaps(@NonNull Stream<? extends Map<K, V>> sources) {
    return sources.map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @SafeVarargs
  public static <K, V> Map<K, V> combineMaps(@NonNull Map<K, V>... sources) {
    return combineMaps(stream(sources));
  }

  public static <T> List<T> combineCollections(@NonNull Stream<? extends Collection<T>> sources) {
    return sources.flatMap(Collection::stream)
        .collect(toList());
  }

  @SafeVarargs
  public static <T> List<T> combineCollections(@NonNull Collection<T>... sources) {
    return combineCollections(stream(sources));
  }

  public static <T> Set<T> combineCollectionsToSet(@NonNull Stream<? extends Collection<T>> sources) {
    return sources.flatMap(Collection::stream)
        .collect(toSet());
  }

  @SafeVarargs
  public static <T> Set<T> combineCollectionsToSet(@NonNull Collection<T>... sources) {
    return combineCollectionsToSet(stream(sources));
  }

  /*
   * Helpers
   */
  private static <T> Stream<T> stream(Iterable<T> iterable, boolean inParallel) {
    return StreamSupport.stream(iterable.spliterator(), inParallel);
  }

}
