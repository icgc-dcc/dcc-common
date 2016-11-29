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
package org.icgc.dcc.common.ega.dataset;

import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import org.icgc.dcc.common.ega.client.EGAAPIClient;
import org.icgc.dcc.common.ega.client.EGAFTPClient;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class EGADatasetMetaArchiveResolver {

  /**
   * Constants.
   */
  public static final String DEFAULT_API_URL = "http://ega.ebi.ac.uk/ega/rest/download/v2";
  private static final EGADatasetMetaArchiveReader ARCHIVE_READER = new EGADatasetMetaArchiveReader();

  /**
   * Dependencies.
   */
  @NonNull
  private final EGAAPIClient api;
  @NonNull
  private final EGAFTPClient ftp;

  public EGADatasetMetaArchiveResolver() {
    this(new EGAAPIClient().login(), new EGAFTPClient());
  }

  public Set<String> resolveDatasetIds() {
    val datasetIds = new TreeSet<String>();
    datasetIds.addAll(ftp.getDatasetIds());
    datasetIds.addAll(api.getDatasetIds());

    return datasetIds;
  }

  public EGADatasetMetaArchive resolveArchive(@NonNull String datasetId) {
    val url = resolveUrl(datasetId);
    return ARCHIVE_READER.read(datasetId, url);
  }

  protected URL resolveUrl(String datasetId) {
    if (ftp.hasDatasetId(datasetId)) {
      return ftp.getArchiveURL(datasetId);
    } else {
      return api.getArchiveURL(datasetId);
    }
  }

}
