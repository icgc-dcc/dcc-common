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

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

/**
 * EGA catalog API client.
 * <p>
 * The catalog API is currently in test.
 */
@RequiredArgsConstructor
public class EGACatalogClient {

  /**
   * Constants.
   */

  // Older version
  private static final String OLD_API_URL = "https://ega.crg.eu/requesterportal/v1/";
  @SuppressWarnings("unused")
  private static final String NEW_API_URL = "https://egatest.crg.eu/metadata/v2/";
  private static final String DEFAULT_API_URL = OLD_API_URL;

  private static final int READ_TIMEOUT = (int) SECONDS.toMillis(30);

  /**
   * Configuration.
   */
  @NonNull
  private final String url;

  public EGACatalogClient() {
    this(DEFAULT_API_URL);
  }

  public ObjectNode getDAC(@NonNull String dacId) {
    return get("dacs", dacId);
  }

  public ObjectNode getDataset(@NonNull String datasetId) {
    return get("datasets", datasetId);
  }

  public ArrayNode getFilesByDataset(@NonNull String datasetId) {
    return list("files", query().limit(0).queryBy("dataset").queryId(datasetId));
  }

  private ObjectNode get(String resource, String id) {
    return (ObjectNode) read(resource + "/" + id).path(0);
  }

  private ArrayNode list(String path, Query.QueryBuilder builder) {
    val query = builder.build();

    // Params
    val map = Maps.<String, Object> newLinkedHashMap();
    if (query.getSkip() != null) map.put("skip", query.getSkip());
    if (query.getLimit() != null) map.put("limit", query.getLimit());
    if (query.getQueryBy() != null) map.put("queryBy", query.getQueryBy());
    if (query.getQueryId() != null) map.put("queryId", query.getQueryId());
    val params = Joiner.on('&').withKeyValueSeparator("=").join(map);

    // URL
    val parts = Sets.<String> newLinkedHashSet();
    parts.add(path);
    if (!isNullOrEmpty(params)) parts.add(params);
    val url = Joiner.on('?').join(parts);

    return read(url);
  }

  @SneakyThrows
  private ArrayNode read(String path) {
    val connection = (HttpsURLConnection) new URL(url + path).openConnection();
    connection.setReadTimeout(READ_TIMEOUT);
    connection.setConnectTimeout(READ_TIMEOUT);
    val response = DEFAULT.readValue(connection.getInputStream(), CatalogResponse.class);

    val header = response.getHeader();

    if ("404".equals(header.getCode())) {
      throw new EGAEntityNotFoundException("Not found %s: %s", path, header);
    }

    if (!"200".equals(header.getCode())) {
      throw new EGAClientException("Error getting %s: %s", path, header);
    }

    return response.getResponse().getResult();
  }

  private static Query.QueryBuilder query() {
    return Query.builder();
  }

  @Value
  @Builder
  public static class Query {

    Integer skip;
    Integer limit;
    String queryBy;
    String queryId;

  }

  @Data
  public static class CatalogResponse {

    Header header;
    Response response;

  }

  @Data
  public static class Header {

    String apiVersion;
    String code;
    String service;
    String developerMessage;
    String userMessage;
    String errorCode;
    String docLink;
    String errorStack;

  }

  @Data
  public static class Response {

    int numTotalResults;
    String resultType;
    ArrayNode result;

  }

}
