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

import static lombok.AccessLevel.PRIVATE;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.icgc.dcc.common.core.util.function.Consumers.FourConsumer;
import org.icgc.dcc.common.core.util.function.Consumers.ThreeConsumer;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
public final class Binding<T, U> {

  public static <T, U> Consumer<T> bind(@NonNull BiConsumer<? super T, U> c, U arg2) {
    return (arg1) -> c.accept(arg1, arg2);
  }

  public static <T, U, V> Consumer<T> bind(@NonNull ThreeConsumer<? super T, U, V> c, U arg2, V arg3) {
    return (arg1) -> c.accept(arg1, arg2, arg3);
  }

  public static <T, U, V, W> Consumer<T> bind(@NonNull FourConsumer<? super T, U, V, W> c, U arg2, V arg3, W arg4) {
    return (arg1) -> c.accept(arg1, arg2, arg3, arg4);
  }

}
