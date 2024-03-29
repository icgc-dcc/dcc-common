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
package org.icgc.dcc.common.ega.client;

import org.icgc.dcc.common.ega.dataset.EGADatasetMetaArchiveReader;
import org.junit.Ignore;
import org.junit.Test;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Ignore("For development only")
public class EGAFTPClientTest {

  EGAFTPClient client = new EGAFTPClient();

  @Test
  public void testGetDatasetIds() throws Exception {
    val dataSetIds = client.getDatasetIds();
    log.info("dataSetIds: {}", dataSetIds);

    for (val dataSetId : dataSetIds) {
      log.info("Reading: {}", dataSetId);
      val url = client.getArchiveURL(dataSetId);
      log.info("URL: {}", url);

      val reader = new EGADatasetMetaArchiveReader();
      val archive = reader.read(dataSetId, url);
      log.info("Dataset: {}", archive);
    }
  }

  @Test
  public void testGetListing() throws Exception {
    val items = client.getListing();
    log.info("Listing:");

    for (val item : items) {
      log.info("Item: {}", item);
    }
  }

}
