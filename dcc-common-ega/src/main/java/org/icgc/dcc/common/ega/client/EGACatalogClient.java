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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

/**
 * EGA catalog API client for https://egatest.crg.eu/metadata/v2
 */
@RequiredArgsConstructor
public class EGACatalogClient {

  /**
   * Constants.
   */
  // https://ega.crg.eu/requesterportal/v2/
  private static final String DEFAULT_API_URL = "https://egatest.crg.eu/metadata/v2/";

  private static final int READ_TIMEOUT = (int) SECONDS.toMillis(10);

  @NonNull
  private final String url;

  public EGACatalogClient() {
    this(DEFAULT_API_URL);
  }

  public CatalogResponse getDAC(@NonNull String dacId) {
    return get("dacs/" + dacId);
  }

  public CatalogResponse getDataset(@NonNull String datasetId) {
    return get("datasets/" + datasetId);
  }

  public CatalogResponse getFilesByDataset(@NonNull String datasetId) {
    return get("files?queryBy=dataset&queryId=" + datasetId);
  }

  private CatalogResponse get(String path) {
    val connection = openConnection(path);
    return readResponse(connection);
  }

  @SneakyThrows
  private HttpURLConnection openConnection(String path) {
    val connection = (HttpsURLConnection) new URL(url + path).openConnection();
    connection.setReadTimeout(READ_TIMEOUT);
    connection.setConnectTimeout(READ_TIMEOUT);

    return connection;
  }

  @SneakyThrows
  private static CatalogResponse readResponse(URLConnection connection) {
    return DEFAULT.readValue(connection.getInputStream(), CatalogResponse.class);
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
