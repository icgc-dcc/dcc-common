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
package org.icgc.dcc.dcc.common.es.util;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.common.unit.ByteSizeUnit.MB;

import org.elasticsearch.common.unit.ByteSizeValue;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class BulkProcessorConfiguration {

  public static final int DEFAULT_BULK_SIZE_MB = 36;
  public static final ByteSizeValue DEFAULT_BULK_SIZE = new ByteSizeValue(DEFAULT_BULK_SIZE_MB, MB);

  public static ByteSizeValue getBulkSize(Integer bulkSizeMb) {
    int bulkSizeValue = isNull(bulkSizeMb) ? DEFAULT_BULK_SIZE_MB : bulkSizeMb;

    return getBulkSize(bulkSizeValue);
  }

  public static ByteSizeValue getBulkSize(int bulkSizeMb) {
    return bulkSizeMb == DEFAULT_BULK_SIZE_MB ? DEFAULT_BULK_SIZE : new ByteSizeValue(bulkSizeMb, MB);
  }

}
