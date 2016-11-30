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
package org.icgc.dcc.common.ega.dump;

import static com.google.common.base.Strings.repeat;
import static com.google.common.base.Throwables.getCausalChain;
import static java.util.stream.Collectors.joining;
import static org.icgc.dcc.common.core.io.Files2.getHomeDir;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.icgc.dcc.common.ega.client.EGAAPIClient;
import org.icgc.dcc.common.ega.dataset.EGADatasetMetaArchiveResolver;
import org.icgc.dcc.common.ega.dataset.EGADatasetMetaReader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility for creating a dump of all EGA metadata.
 */
@Slf4j
@RequiredArgsConstructor
public class EGAMetadataDumper {

  public static void main(String[] args) {
    val dumper = new EGAMetadataDumper();

    val date = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());
    val file = new File(getHomeDir(), "icgc-ega-datasets." + date + ".jsonl");

    dumper.create(file);
  }

  /**
   * Dependencies.
   */
  @NonNull
  private final EGADatasetMetaReader reader;

  public EGAMetadataDumper() {
    this(createReader());
  }

  @SneakyThrows
  public void create(@NonNull File file) {
    val datasets = read();
    write(file, datasets);

    report();
  }

  public void report() {
    banner("Error report:");
    int i = 1;
    for (val error : reader.getErrors()) {
      log.error("   [{}] Error: {}", i++, getErrorMessage(error));
    }
  }

  private Stream<EGADatasetDump> read() {
    return reader.readDatasets();
  }

  private void write(File file, Stream<EGADatasetDump> datasets) {
    new EGAMetadataDumpWriter().write(file, datasets);
  }

  private static EGADatasetMetaReader createReader() {
    return new EGADatasetMetaReader(new EGAAPIClient().login(), new EGADatasetMetaArchiveResolver());
  }

  private static void banner(String message) {
    val line = repeat("-", 100);
    log.info(line);
    log.info(message);
    log.info(line);
  }

  private static String getErrorMessage(Exception e) {
    return getCausalChain(e).stream().map(Throwable::getMessage).collect(joining(": "));
  }

}
