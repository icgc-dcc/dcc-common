/*
 * Copyright (c) 2014 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.core.util;

import static lombok.AccessLevel.PUBLIC;

import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.Value;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * 3-dimensional equivalent to {@link Table} ({@link HashBasedTable} for now).
 * <p>
 * TODO: further delegation (as needed).
 */
@NoArgsConstructor(access = PUBLIC, staticName = "create")
public class ThreeDimensionalTable<R, C, D, V> {

  private final Table<Key<R, C>, D, V> table = HashBasedTable.create();

  public V put(R r, C c, D d, V v) {
    return table.put(new Key<R, C>(r, c), d, v);
  }

  public V get(R r, C c, D d) {
    return table.get(new Key<R, C>(r, c), d);
  }

  public Map<D, V> depth(R r, C c) {
    return table.row(new Key<R, C>(r, c));
  }

  @Value
  private static class Key<R, C> {

    R r;
    C c;

  }

}
