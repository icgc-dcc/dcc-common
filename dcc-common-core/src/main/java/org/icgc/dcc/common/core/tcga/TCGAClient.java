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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.icgc.dcc.common.core.util.Formats.formatCount;
import static org.icgc.dcc.common.core.util.Joiners.COMMA;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TCGAClient {

  /**
   * Constants
   */
  private static final String DEFAULT_API_URL = "https://gdc-api.nci.nih.gov/legacy";

  private static final int MAX_ATTEMPTS = 10;
  private static final int MAX_CASES = 1_000_000; // This is known to be greater than the max that exist
  private static final int READ_TIMEOUT = (int) SECONDS.toMillis(60);

  private static final String APPLICATION_JSON = "application/json";

  private static final List<String> FIELD_NAMES =
      ImmutableList.of(
          "case_id",
          "submitter_id",
          "samples.sample_id",
          "samples.submitter_id",
          "samples.portions.analytes.aliquots.aliquot_id",
          "samples.portions.analytes.aliquots.submitter_id");

  /**
   * Configuration.
   */
  @NonNull
  private final String url;
  @Getter(lazy = true, value = PRIVATE)
  @Accessors(fluent = true)
  private final BiMap<String, String> mapping = createMapping(); // UUID -> barcode

  public TCGAClient() {
    this(DEFAULT_API_URL);
  }

  @NonNull
  public String getUUID(String barcode) {
    return mapping().inverse().get(barcode);
  }

  @NonNull
  public Map<String, String> getUUIDs(Set<String> barcodes) {
    val uuids = ImmutableMap.<String, String> builder();
    for (val barcode : barcodes) {
      val uuid = getUUID(barcode);
      checkState(uuid != null, "Could not find UUID for barcode '%s'", barcode);

      uuids.put(barcode, uuid);
    }

    return uuids.build();
  }

  @NonNull
  public String getBarcode(String uuid) {
    return mapping().get(uuid);
  }

  @NonNull
  public Map<String, String> getBarcodes(Set<String> uuids) {
    val barcodes = ImmutableMap.<String, String> builder();
    for (val uuid : uuids) {
      val barcode = getBarcode(uuid);
      checkState(barcode != null, "Could not find barcode for UUID '%s'", uuid);

      barcodes.put(uuid, barcode);
    }

    return barcodes.build();
  }

  private BiMap<String, String> createMapping() {
    log.info("Creating UUID <-> barcode mapping...");
    val watch = Stopwatch.createStarted();
    val cases = readCases();

    // Allow for lookup by barcode or UUID value
    val mapping = HashBiMap.<String, String> create();
    int caseCount = 0;
    while (cases.hasNext()) {
      caseCount++;
      val caze = cases.next();

      mapping.put(caze.get("case_id").textValue(), caze.get("submitter_id").textValue());
      for (val sample : caze.path("samples")) {
        mapping.put(sample.get("sample_id").textValue(), sample.get("submitter_id").textValue());

        for (val portion : sample.path("portions")) {
          for (val analyte : portion.path("analytes")) {
            for (val aliquot : analyte.path("aliquots")) {
              mapping.put(aliquot.get("aliquot_id").textValue(), aliquot.get("submitter_id").textValue());
            }
          }
        }
      }
    }

    log.info("Finished creating {} mappings from {} cases in {}", formatCount(mapping), formatCount(caseCount), watch);
    return mapping;
  }

  private Iterator<JsonNode> readCases() {
    val params = Maps.<String, Object> newLinkedHashMap();
    params.put("size", MAX_CASES);
    params.put("fields", COMMA.join(FIELD_NAMES));

    val request = "/cases" + "?" + Joiner.on('&').withKeyValueSeparator("=").join(params);

    int attempts = 0;
    while (++attempts <= MAX_ATTEMPTS) {
      try {
        val connection = openConnection(request);
        val response = readResponse(connection);

        return response;
      } catch (SocketTimeoutException e) {
        log.warn("Socket timeout for {} after {} attempt(s)", request, attempts);
      }
    }

    throw new IllegalStateException("Could not get " + request);
  }

  @SneakyThrows
  private HttpURLConnection openConnection(String path) throws SocketTimeoutException {
    val request = new URL(url + path);

    log.debug("Request: {}", request);
    val connection = (HttpsURLConnection) request.openConnection();
    connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
    connection.setReadTimeout(READ_TIMEOUT);
    connection.setConnectTimeout(READ_TIMEOUT);

    return connection;
  }

  @SneakyThrows
  private static Iterator<JsonNode> readResponse(HttpURLConnection connection) {
    try {
      connection.connect();
      val actualResponseCode = connection.getResponseCode();
      val actualContentType = connection.getContentType();

      val expectedResponseCode = 200;
      val expectedContentType = APPLICATION_JSON;

      checkState(actualResponseCode == expectedResponseCode && expectedContentType.equals(actualContentType),
          "Expected %s reponse code with content type '%s' from %s but got %s reponse code with content type '%s' instead.",
          expectedResponseCode, expectedContentType, connection.getURL(), actualResponseCode, actualContentType);

      // Stream parse for speed and memory efficiency
      val parser = DEFAULT.getFactory().createParser(connection.getInputStream());

      while (true) {
        parser.nextToken();
        if ("hits".equals(parser.getCurrentName())) {
          parser.nextToken();
          parser.nextToken();
          break;
        }
      }

      return parser.readValuesAs(JsonNode.class);
    } catch (IOException e) {
      throw new RuntimeException("Error trying to read response from " + connection.getURL() + ": ", e);
    }
  }

}
