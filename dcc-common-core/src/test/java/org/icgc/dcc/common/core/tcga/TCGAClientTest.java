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
package org.icgc.dcc.common.core.tcga;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import lombok.val;

public class TCGAClientTest {

  /**
   * CUT.
   * <p>
   * Static because will data will be cached for reuse.
   */
  private static TCGAClient client = new TCGAClient();

  @Test
  public void testGetUUID() throws Exception {
    val barcode = "TCGA-EB-A85I";
    val uuid = client.getUUID(barcode);

    assertThat(uuid).isEqualTo("50dc9c63-c491-4afc-ab12-74b40eba58b6");
  }

  @Test
  public void testGetUUIDsSingle() throws Exception {
    val barcodes = ImmutableSet.of("TCGA-EB-A85I");
    val uuids = client.getUUIDs(barcodes);

    assertThat(uuids).isEqualTo(ImmutableMap.of(
        "TCGA-EB-A85I", "50dc9c63-c491-4afc-ab12-74b40eba58b6"));
  }

  @Test
  public void testGetUUIDsMultiple() throws Exception {
    val barcodes = ImmutableSet.of("TCGA-EB-A85I", "TCGA-VM-A8CD-10A-01D-A366-01");
    val uuids = client.getUUIDs(barcodes);

    assertThat(uuids).isEqualTo(ImmutableMap.of(
        "TCGA-EB-A85I", "50dc9c63-c491-4afc-ab12-74b40eba58b6",
        "TCGA-VM-A8CD-10A-01D-A366-01", "8477135d-aaf2-45c5-ab1d-f642fa93d03f"));
  }

  @Test
  public void testGetBarcode() throws Exception {
    val uuid = "50dc9c63-c491-4afc-ab12-74b40eba58b6";
    val barcode = client.getBarcode(uuid);

    assertThat(barcode).isEqualTo("TCGA-EB-A85I");
  }

  @Test
  public void testGetBarcodesSingle() throws Exception {
    val uuids = ImmutableSet.of("50dc9c63-c491-4afc-ab12-74b40eba58b6");
    val barcodes = client.getBarcodes(uuids);

    assertThat(barcodes).isEqualTo(ImmutableMap.of(
        "50dc9c63-c491-4afc-ab12-74b40eba58b6", "TCGA-EB-A85I"));
  }

  @Test
  public void testGetBarcodesMultiple() throws Exception {
    val uuids = ImmutableSet.of("50dc9c63-c491-4afc-ab12-74b40eba58b6", "8477135d-aaf2-45c5-ab1d-f642fa93d03f");
    val barcodes = client.getBarcodes(uuids);

    assertThat(barcodes).isEqualTo(ImmutableMap.of(
        "50dc9c63-c491-4afc-ab12-74b40eba58b6", "TCGA-EB-A85I",
        "8477135d-aaf2-45c5-ab1d-f642fa93d03f", "TCGA-VM-A8CD-10A-01D-A366-01"));
  }

}
