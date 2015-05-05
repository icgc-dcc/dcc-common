/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
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
import lombok.val;

import org.junit.Test;

public class TCGAClientTest {

  TCGAClient client = new TCGAClient();

  @Test
  public void testGetUUID() throws Exception {
    val barcode = "TCGA-5T-A9QA-01A-21-A43F-20";
    val uuid = client.getUUID(barcode);

    assertThat(uuid).isEqualTo("9e71a150-8fd7-466c-96af-aab29520bcdc");
  }

  @Test
  public void testGetBarcode() throws Exception {
    val uuid = "9e71a150-8fd7-466c-96af-aab29520bcdc";
    val barcode = client.getBarcode(uuid);

    assertThat(barcode).isEqualTo("TCGA-5T-A9QA-01A-21-A43F-20");
  }

}
