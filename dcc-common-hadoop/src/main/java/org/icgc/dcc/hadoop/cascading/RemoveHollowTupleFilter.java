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
package org.icgc.dcc.hadoop.cascading;

import org.icgc.dcc.hadoop.cascading.operation.BaseFilter;

import cascading.flow.FlowProcess;
import cascading.operation.FilterCall;
import cascading.tuple.Tuple;

/**
 * TODO: move to a more generic module
 * <p>
 * "hollow" because "empty" would be ambiguous with regard to whether the {@code Tuple} has elements or not, whereas we
 * care whether those elements are null or not instead.
 */
public class RemoveHollowTupleFilter extends BaseFilter<Void> {

  @Override
  public boolean isRemove(@SuppressWarnings("rawtypes") FlowProcess flowProcess, FilterCall<Void> filterCall) {
    Tuple tuple = filterCall.getArguments().getTuple();
    return isHollowTuple(tuple);
  }

  private boolean isHollowTuple(Tuple tuple) {
    return tuple.equals(hollowTuple(tuple.size()));
  }

  private Tuple hollowTuple(int size) {
    return Tuple.size(size);
  }
}