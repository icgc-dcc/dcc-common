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

import static com.google.common.base.Throwables.getCausalChain;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.icgc.dcc.common.core.util.Formats.formatCount;
import static org.icgc.dcc.common.core.util.function.Predicates.isNotNull;
import static org.icgc.dcc.common.ega.core.EGAProjectDatasets.getDatasetProjectCodes;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.icgc.dcc.common.ega.client.EGAAPIClient;
import org.icgc.dcc.common.ega.client.EGACatalogClient;
import org.icgc.dcc.common.ega.dump.EGADatasetDump;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import lombok.Getter;
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
  private final EGACatalogClient catalog = new EGACatalogClient();
  @NonNull
  private final EGAAPIClient api;
  @NonNull
  private final EGADatasetMetaArchiveResolver archiveResolver;

  /**
   * State.
   */
  @Getter
  private final List<Exception> errors = Lists.newArrayList();

  public Stream<EGADatasetDump> readDatasets() {
    val datasetIds = resolveDatasetIds();
    log.info("Resolved {} datasets", formatCount(datasetIds));

    val counter = new AtomicInteger();
    return datasetIds.stream().peek(datasetId -> {
      log.info("[{}/{}] Processing dataset {}...", counter.incrementAndGet(), datasetIds.size(), datasetId);
    }).map(this::readDataset).filter(isNotNull());
  }

  public EGADatasetDump readDataset(@NonNull String datasetId) {
    try {
      val catalog = readCatalog(datasetId);
      val projectCodes = getDatasetProjectCodes(datasetId);
      val files = readDatasetFiles(datasetId);
      val archive = readArchive(datasetId);

      return new EGADatasetDump(datasetId, catalog, projectCodes, files, archive);
    } catch (Exception e) {
      errors.add(e);
      log.error("Exception reading dataset {}: {}", datasetId, getErrorMessage(e));

      return null;
    }
  }

  public List<ObjectNode> readDatasetFiles(String datasetId) {
    try {
      return api.getDatasetFiles(datasetId);
    } catch (Exception e) {
      errors.add(e);
      log.error("Exception reading dataset files {}: {}", datasetId, getErrorMessage(e));

      return emptyList();
    }
  }

  private Set<String> resolveDatasetIds() {
    return archiveResolver.resolveDatasetIds();
  }

  private EGADatasetMetaArchive readArchive(String datasetId) {
    return archiveResolver.resolveArchive(datasetId);
  }

  private ObjectNode readCatalog(String datasetId) {
    try {
      return catalog.getDataset(datasetId);
    } catch (Exception e) {
      log.error("Exception reading dataset {} catalog: {}", datasetId, getErrorMessage(e));
      errors.add(e);

      return null;
    }
  }

  private static String getErrorMessage(Exception e) {
    return getCausalChain(e).stream().map(Throwable::getMessage).collect(joining(": "));
  }

}
