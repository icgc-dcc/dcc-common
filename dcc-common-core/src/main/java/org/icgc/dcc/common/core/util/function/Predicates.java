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
package org.icgc.dcc.common.core.util.function;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

@NoArgsConstructor(access = PRIVATE)
public final class Predicates {

  /**
   * Logically negates a given predicate.
   */
  public static <T> Predicate<T> not(@NonNull Predicate<T> predicate) {
    return predicate.negate();
  }

  /**
   * Logically {@code and}s a given set of predicate.
   */
  @SafeVarargs
  public static <T> Predicate<T> and(@NonNull Predicate<T>... predicates) {
    return Stream.of(predicates).reduce(Predicate::and).orElse(x -> true);
  }

  /**
   * Logically {@code or}s given set of predicate.
   */
  @SafeVarargs
  public static <T> Predicate<T> or(@NonNull Predicate<T>... predicates) {
    return Stream.of(predicates).reduce(Predicate::or).orElse(x -> false);
  }

  /**
   * Returns a predicate that can be used with Java streams to filter unique objects based on a given
   * {@code keyExtractor}.
   * <p>
   * Example:
   * 
   * <code>
   *  list.stream().filter(distinctByKey(Entity::getId)).collect(toImmutableLst());
   * </code>
   */
  public static <T> Predicate<T> distinctByKey(@NonNull Function<? super T, Object> keyExtractor) {
    val seen = new ConcurrentHashMap<Object, Boolean>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), TRUE) == null;
  }

}
