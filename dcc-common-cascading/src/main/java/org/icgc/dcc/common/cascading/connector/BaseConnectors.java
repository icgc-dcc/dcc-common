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
package org.icgc.dcc.common.cascading.connector;

import java.util.Map;

import lombok.NonNull;

import org.icgc.dcc.common.core.collect.Maps2;

import cascading.cascade.CascadeConnector;
import cascading.flow.FlowConnector;

abstract class BaseConnectors implements CascadingConnectors {

  @Override
  public String describe() {
    return describe(getClass());
  }

  @Override
  public CascadeConnector getCascadeConnector() {
    return new CascadeConnector();
  }

  @Override
  public CascadeConnector getCascadeConnector(@NonNull final Map<?, ?> properties) {
    return new CascadeConnector(toObjectsMap(properties));
  }

  @Override
  public FlowConnector getTestFlowConnector() {
    return getFlowConnector(getDefaultProperties());
  }

  protected static Map<Object, Object> toObjectsMap(@NonNull final Map<?, ?> properties) {
    return Maps2.toObjectsMap(properties);
  }

  private static String describe(@NonNull final Class<?> type) {
    return "Using " + type.getSimpleName();
  }

}
