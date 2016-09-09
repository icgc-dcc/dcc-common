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
package org.icgc.dcc.common.tcga.legacy;

import static com.google.common.collect.Iterables.partition;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.icgc.dcc.common.core.util.Joiners.COMMA;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

/**
 * See https://wiki.nci.nih.gov/display/TCGA/TCGA+Barcode+to+UUID+Web+Service+User%27s+Guide
 * <p>
 * No longer available since the sundowning of TCGA.
 */
@Deprecated
@RequiredArgsConstructor
public class LegacyTCGAClient {

  /**
   * Constants.
   */
  private static final String DEFAULT_TCGA_BASE_URL = "https://tcga-data.nci.nih.gov";
  private static final int TCGA_MAX_BATCH_SIZE = 500;

  @NonNull
  private final String baseUrl;

  public LegacyTCGAClient() {
    this(DEFAULT_TCGA_BASE_URL);
  }

  @NonNull
  public String getUUID(String barcode) {
    val mappingUrl = getBarcodeMappingURL(barcode);
    val mapping = getMapping(mappingUrl);

    return getMappingUUID(mapping);
  }

  @NonNull
  public Map<String, String> getUUIDs(Set<String> barcodes) {
    return getIDs(
        getBarcodesMappingsURL(),
        barcodes,
        (JsonNode mapping) -> mapping.get("barcode").textValue(),
        (JsonNode mapping) -> mapping.get("uuid").textValue());
  }

  @NonNull
  public String getBarcode(String uuid) {
    val mappingUrl = getUUIDMappingURL(uuid);
    val mapping = getMapping(mappingUrl);

    return getMappingBarcode(mapping);
  }

  @NonNull
  public Map<String, String> getBarcodes(Set<String> uuids) {
    return getIDs(
        getUUIDMappingsURL(),
        uuids,
        (JsonNode mapping) -> mapping.get("uuid").textValue(),
        (JsonNode mapping) -> mapping.get("barcode").textValue());
  }

  public Map<String, String> getIDs(String mappingsUrl, Set<String> ids, Function<JsonNode, String> key,
      Function<JsonNode, String> value) {
    val map = ImmutableMap.<String, String> builder();
    for (val batch : getBatches(ids)) {
      val mappings = getMappings(mappingsUrl, batch);
      val node = mappings.path("uuidMapping");
      if (node.isArray()) {
        for (val mapping : node) {
          map.put(key.apply(mapping), value.apply(mapping));
        }
      } else {
        map.put(key.apply(node), value.apply(node));
      }
    }

    return map.build();
  }

  private static Iterable<List<String>> getBatches(Set<String> barcodes) {
    return partition(barcodes, TCGA_MAX_BATCH_SIZE);
  }

  private String getBarcodeMappingURL(String barcode) {
    return getMappingURL("/barcode" + "/" + barcode);
  }

  private String getBarcodesMappingsURL() {
    return getMappingURL("/barcode/batch");
  }

  private String getUUIDMappingURL(String uuid) {
    return getMappingURL("/uuid" + "/" + uuid);
  }

  private String getUUIDMappingsURL() {
    return getMappingURL("/uuid/batch");
  }

  private String getMappingURL(String path) {
    return baseUrl + "/uuid/uuidws/mapping" + "/json" + path;
  }

  private static String getMappingUUID(JsonNode mapping) {
    return mapping.path("uuidMapping").path("uuid").asText();
  }

  private static String getMappingBarcode(JsonNode mapping) {
    return mapping.path("barcode").asText();
  }

  @SneakyThrows
  private static JsonNode getMapping(String url) {
    return DEFAULT.readTree(new URL(url));
  }

  @SneakyThrows
  private static JsonNode getMappings(String url, Iterable<String> ids) {
    val body = COMMA.join(ids);
    val connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty(CONTENT_TYPE, "text/plain");
    connection.setRequestProperty(CONTENT_LENGTH, Integer.toString(body.length()));
    connection.setDoOutput(true);
    connection.getOutputStream().write(body.getBytes(UTF_8));

    return DEFAULT.readTree(connection.getInputStream());
  }

}
