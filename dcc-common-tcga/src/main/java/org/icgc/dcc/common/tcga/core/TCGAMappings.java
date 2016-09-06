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
package org.icgc.dcc.common.tcga.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class TCGAMappings implements Iterable<Entry<String, String>> {

  @NonNull
  private final BiMap<String, String> mapping;

  @SuppressWarnings("unchecked")
  public TCGAMappings(Properties mapping) {
    this.mapping = HashBiMap.<String, String> create();
    this.mapping.putAll((Map<String, String>) (Object) mapping);
  }

  @NonNull
  public String getUUID(String barcode) {
    return mapping.inverse().get(barcode);
  }

  @NonNull
  public Map<String, String> getUUIDs(Set<String> barcodes) {
    val uuids = ImmutableMap.<String, String> builder();
    for (val barcode : barcodes) {
      val uuid = getUUID(barcode);

      uuids.put(barcode, uuid);
    }

    return uuids.build();
  }

  @NonNull
  public String getBarcode(String uuid) {
    return mapping.get(uuid);
  }

  @NonNull
  public Map<String, String> getBarcodes(Set<String> uuids) {
    val barcodes = ImmutableMap.<String, String> builder();
    for (val uuid : uuids) {
      val barcode = getBarcode(uuid);

      barcodes.put(uuid, barcode);
    }

    return barcodes.build();
  }

  @Override
  public Iterator<Entry<String, String>> iterator() {
    return mapping.entrySet().iterator();
  }

}
