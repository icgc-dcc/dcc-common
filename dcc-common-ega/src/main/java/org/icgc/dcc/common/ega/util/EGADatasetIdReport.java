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
package org.icgc.dcc.common.ega.util;

import static com.google.common.collect.Sets.newTreeSet;

import java.util.Set;
import java.util.TreeSet;

import org.icgc.dcc.common.ega.client.EGAAPIClient;
import org.icgc.dcc.common.ega.client.EGACatalogClient;
import org.icgc.dcc.common.ega.client.EGAFTPClient;

import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EGADatasetIdReport {

  public static void main(String[] args) {
    val ftp = new EGAFTPClient();
    val api = new EGAAPIClient().login();
    val cat = new EGACatalogClient();

    val ftpDatasetIds = newTreeSet(ftp.getDatasetIds());
    val apiDatasetIds = newTreeSet(api.getDatasetIds());
    val datasetIds = combineDatasetIds(ftpDatasetIds, apiDatasetIds);

    val catDatasetIds = getCatalogDatasetIds(cat, datasetIds);

    val missingFtp = Sets.difference(datasetIds, apiDatasetIds);
    val missingApi = Sets.difference(datasetIds, ftpDatasetIds);
    val missingCat = Sets.difference(datasetIds, catDatasetIds);

    log.info("Missing in Catalog:");
    for (val entry : missingCat) {
      log.info(" - {}", entry);
    }

    log.info("Missing in API:");
    for (val entry : missingApi) {
      log.info(" - {}", entry);
    }

    log.info("Missing in FTP:");
    for (val entry : missingFtp) {
      log.info(" - {}", entry);
    }

  }

  public static Set<String> combineDatasetIds(Set<String> ftpDatasetIds, Set<String> apiDatasetIds) {
    val datasetIds = new TreeSet<String>();
    datasetIds.addAll(ftpDatasetIds);
    datasetIds.addAll(apiDatasetIds);

    return datasetIds;
  }

  public static Set<String> getCatalogDatasetIds(EGACatalogClient cat, Set<String> datasetIds) {
    log.info("EGA Box mapping:");
    val catDatasetIds = new TreeSet<String>();

    val boxNumbers = TreeMultimap.<String, String> create();
    val submissionIds = TreeMultimap.<String, String> create();

    for (val datasetId : datasetIds) {
      try {
        val dataset = cat.getDataset(datasetId);
        catDatasetIds.add(datasetId);

        val boxNumber = dataset.path("submitterId").textValue();
        val submissionId = dataset.path("submissionId").textValue();

        boxNumbers.put(boxNumber, datasetId);
        submissionIds.put(submissionId, datasetId);
        log.info(" - {} = {} ({})", datasetId, boxNumber, submissionId);
      } catch (Exception e) {
        log.error(" - {} = {}", datasetId, e.getMessage());
      }
    }

    log.info("Reverse EGA Box mapping:");
    for (val boxNumber : boxNumbers.keySet()) {
      log.info(" - {} = {}", boxNumber, boxNumbers.get(boxNumber));
    }
    log.info("Reverse submissionId mapping:");
    for (val submissionId : submissionIds.keySet()) {
      log.info(" - {} = {}", submissionId, submissionIds.get(submissionId));
    }

    return catDatasetIds;
  }

}
