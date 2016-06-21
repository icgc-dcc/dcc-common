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
package org.icgc.dcc.common.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DownloadDataTypeTest {

  @Test
  public void initTest() {
    assertThat(DownloadDataType.canCreateFrom("donor")).isFalse();
    assertThat(DownloadDataType.canCreateFrom("DONOR")).isTrue();
  }

  @Test
  public void testFrom() throws Exception {
    assertThat(DownloadDataType.from("donor", false)).isEqualTo(DownloadDataType.DONOR);
    assertThat(DownloadDataType.from("donor", true)).isEqualTo(DownloadDataType.DONOR);
    assertThat(DownloadDataType.from("ssm", true)).isEqualTo(DownloadDataType.SSM_CONTROLLED);
    assertThat(DownloadDataType.from("ssm", false)).isEqualTo(DownloadDataType.SSM_OPEN);
    assertThat(DownloadDataType.from("sgv", true)).isEqualTo(DownloadDataType.SGV_CONTROLLED);
    assertThat(DownloadDataType.from("sgv", false)).isEqualTo(DownloadDataType.SGV_CONTROLLED);
  }

}
