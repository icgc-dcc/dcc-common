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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.object;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.icgc.dcc.common.ega.util.EGACertificates;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Client for the main EGA API.
 * <p>
 * Currently only covers metadata related endpoints.
 * 
 * @see https://www.ebi.ac.uk/ega/about/your_EGA_account/download_streaming_client#API_overview
 */
@Slf4j
@RequiredArgsConstructor
public class EGAAPIClient {

  /**
   * Constants - Defaults
   */
  private static final String DEFAULT_API_URL = "https://ega.ebi.ac.uk/ega/rest/access/v2";
  private static final boolean DEFAULT_RECONNECT = true;
  private static final boolean DEFAULT_RETRY_NOT_AUTHORIZED = false;

  /**
   * Constants - General
   */
  private static final SSLSocketFactory SSL_SOCKET_FACTORY = createSSLSocketFactory();

  private static final int MAX_ATTEMPTS = 50;
  private static final int READ_TIMEOUT = (int) SECONDS.toMillis(5);

  private static final String METHOD_POST = "POST";
  private static final String APPLICATION_JSON = "application/json";

  public EGAAPIClient() {
    this(System.getProperty("ega.username"), System.getProperty("ega.password"));
  }

  public EGAAPIClient(String userName, String password) {
    this(DEFAULT_API_URL, userName, password, DEFAULT_RECONNECT, DEFAULT_RETRY_NOT_AUTHORIZED);
  }

  public EGAAPIClient(String userName, String password, boolean reconnect) {
    this(DEFAULT_API_URL, userName, password, reconnect, DEFAULT_RETRY_NOT_AUTHORIZED);
  }

  public EGAAPIClient(String userName, String password, boolean reconnect, boolean retryNotAuthorized) {
    this(DEFAULT_API_URL, userName, password, reconnect, retryNotAuthorized);
  }

  /**
   * Configuration.
   */
  @NonNull
  private final String url;
  @NonNull
  private final String userName;
  @NonNull
  private final String password;
  private final boolean reconnect; // Pretty much always needed
  private final boolean retryNotAuthorized; // Useful for when server is in a flakey state

  /**
   * State.
   */
  @Getter(onMethod = @__(@Synchronized))
  private String sessionId;
  @Getter(onMethod = @__(@Synchronized))
  private int timeoutCount;
  @Getter(onMethod = @__(@Synchronized))
  private int reconnectCount;
  @Getter(onMethod = @__(@Synchronized))
  private int errorCount;

  @SneakyThrows
  @Synchronized
  public EGAAPIClient login() {
    int attempts = 0;
    while (++attempts <= MAX_ATTEMPTS) {
      try {
        val path = "/users/login";
        val connection = openConnection(path);
        connection.setRequestMethod(METHOD_POST);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setDoOutput(true);

        val request = createLoginRequest(userName, password);
        connection.setRequestProperty(CONTENT_LENGTH, Integer.toString(request.length()));
        connection.getOutputStream().write(request.getBytes(UTF_8));

        val response = readResponse(connection);
        checkResponse(path, response);

        this.sessionId = getSessionId(response);

        return this;
      } catch (IllegalStateException e) {
        log.warn("Invalid login after {} attempt(s): {}", attempts, e.getMessage());
      } catch (Exception e) {
        log.error("Error logging in: {}", e.getMessage());
        throw e;
      }
    }

    throw new IllegalStateException("Could login with user " + userName);
  }

  @Synchronized
  public List<String> getDatasetIds() {
    return get("/datasets", new TypeReference<List<String>>() {});
  }

  @Synchronized
  public List<ObjectNode> getDatasetFiles(@NonNull String datasetId) {
    return get("/datasets/" + datasetId + "/files", new TypeReference<List<ObjectNode>>() {});
  }

  @Synchronized
  public ArrayNode getFile(@NonNull String fileId) {
    return get("/files/" + fileId, new TypeReference<ArrayNode>() {});
  }

  private <T> T get(String path, TypeReference<T> responseType) {
    checkState(isSessionActive(), "You must login first before calling API methods.");

    int attempts = 0;
    while (++attempts <= MAX_ATTEMPTS) {
      try {
        val connection = openConnection(path + "?session=" + sessionId);

        val response = readResponse(connection);
        val code = getCode(response);

        if (isRetryLogin(code)) {
          log.warn("Lost session, reconnecting... {}", response);
          reconnectCount++;
          login();

          // Recurse
          return get(path, responseType);
        }

        checkResponse(path, response);
        return DEFAULT.convertValue(getResult(response), responseType);
      } catch (SocketTimeoutException e) {
        timeoutCount++;
        log.warn("*** Attempt [{}/{}] failed: Socket timeout requesting {}", attempts, MAX_ATTEMPTS, path);
      } catch (@SuppressWarnings("hiding") IOException e) {
        // This could happen due to 500 in the reading of the json response. Seems transient...
        errorCount++;
        log.warn("*** Attempt [{}/{}] failed: Error requesting {}: {}", attempts, MAX_ATTEMPTS, path, e.getMessage());
      }

      // Let tensions settle...
      sleepUninterruptibly(attempts * 100, TimeUnit.MILLISECONDS);
    }

    throw new IllegalStateException("Could not get " + path);
  }

  @SneakyThrows
  private HttpURLConnection openConnection(String path) throws SocketTimeoutException {
    val connection = (HttpsURLConnection) new URL(url + path).openConnection();
    connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
    connection.setReadTimeout(READ_TIMEOUT);
    connection.setConnectTimeout(READ_TIMEOUT);
    connection.setSSLSocketFactory(SSL_SOCKET_FACTORY);

    return connection;
  }

  private boolean isSessionActive() {
    return sessionId != null;
  }

  private boolean isRetryLogin(int code) {
    return (isSessionExpired(code) || retryNotAuthorized && isNotAuthorized(code)) && reconnect;
  }

  private static void checkResponse(String path, JsonNode response) {
    val code = getCode(response);
    if (isNotAuthorized(code)) {
      throw new EGANotAuthorizedException("Not authorized to access entity at path %s: %s", path, response);
    }
    if (isNotFound(code)) {
      throw new EGAEntityNotFoundException("Could not find entity at path %s: %s", path, response);
    }
    if (!isOk(code)) {
      throw new EGAClientException("Expected OK response for path %s, got %s: %s", path, code, response);
    }
  }

  @SneakyThrows
  private static JsonNode readResponse(URLConnection connection) {
    return DEFAULT.readTree(connection.getInputStream());
  }

  private static String createLoginRequest(String userName, String password) {
    return "loginrequest=" + object("username", userName, "password", password);
  }

  private static String getSessionId(JsonNode response) {
    return getResult(response).path(1).textValue();
  }

  private static int getCode(JsonNode response) {
    return response.path("header").path("code").asInt();
  }

  private static JsonNode getResult(JsonNode response) {
    return response.path("response").path("result");
  }

  private static boolean isOk(final int code) {
    return code == HTTP_OK;
  }

  private static boolean isNotFound(final int code) {
    return code == HTTP_NOT_FOUND;
  }

  private static boolean isNotAuthorized(int code) {
    return code == HTTP_UNAUTHORIZED;
  }

  private static boolean isSessionExpired(int code) {
    return code == 991;
  }

  @SneakyThrows
  private static SSLSocketFactory createSSLSocketFactory() {
    val trustManagerFactory = TrustManagerFactory.getInstance("X509"); // Returns a new instance
    trustManagerFactory.init(EGACertificates.getKeyStore());
    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

    val context = SSLContext.getInstance("SSL"); // Returns a new instance
    context.init(null, trustManagers, null);

    return context.getSocketFactory();
  }

}
