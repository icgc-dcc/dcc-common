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
package org.icgc.dcc.common.ega.reader;

import static com.google.common.collect.Sets.newTreeSet;
import static org.icgc.dcc.common.core.util.function.Predicates.isNotNull;
import static org.icgc.dcc.common.ega.core.EGAProjectDatasets.getDatasetProjectCodes;

import java.util.stream.Stream;

import org.icgc.dcc.common.ega.archive.EGADatasetMetaArchiveResolver;
import org.icgc.dcc.common.ega.client.EGACatalogClient;
import org.icgc.dcc.common.ega.client.EGAAPIClient;
import org.icgc.dcc.common.ega.model.EGADatasetMeta;
import org.icgc.dcc.common.ega.model.EGADatasetMetaArchive;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Lazily reads all metadata for ICGC associated projects into a stream.
 */
@Slf4j
@RequiredArgsConstructor
public class EGADatasetMetaReader {

  /**
   * Dependencies.
   */
  @NonNull
  private final EGAAPIClient client;
  @NonNull
  private final EGADatasetMetaArchiveResolver archiveResolver;

  public Stream<EGADatasetMeta> readDatasets() {
    val datasetIds = client.getDatasetIds();
    val effectiveDatasetIds = newTreeSet(datasetIds);
    if (effectiveDatasetIds.size() != datasetIds.size()) {
      log.warn("Data sets include duplicates: {}", datasetIds);
    }

    return effectiveDatasetIds.stream().map(this::readDataset).filter(isNotNull());
  }

  public EGADatasetMeta readDataset(@NonNull String datasetId) {
    try {
      val catalog = readCatalog(datasetId);
      val projectCodes = getDatasetProjectCodes(datasetId);
      val files = client.getDatasetFiles(datasetId);
      val archive = readArchive(datasetId);

      return new EGADatasetMeta(datasetId, catalog, projectCodes, files, archive);
    } catch (Exception e) {
      log.error("Exception reading dataset " + datasetId, e);
    }

    return null;
  }

  private EGADatasetMetaArchive readArchive(String datasetId) {
    return archiveResolver.resolveArchive(datasetId);
  }

  private ObjectNode readCatalog(String datasetId) {
    return new EGACatalogClient().getDataset(datasetId);
  }

}
