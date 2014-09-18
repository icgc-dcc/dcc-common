/*
 * Copyright (c) 2014 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.icgc.client.impl.cud;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.util.Map;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.JsonNode;
import org.icgc.dcc.icgc.client.api.ICGCAccessException;
import org.icgc.dcc.icgc.client.api.ICGCClientConfig;
import org.icgc.dcc.icgc.client.api.ICGCUnknownException;
import org.icgc.dcc.icgc.client.api.cud.CUDClient;
import org.icgc.dcc.icgc.client.api.cud.User;
import org.icgc.dcc.icgc.client.impl.BaseICGCClient;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

@Slf4j
public class DefaultCUDClient extends BaseICGCClient implements CUDClient {

  /** Although this parameter is related to userName, the webdev team calls it 'email' */
  private static final String EMAIL_QUERY_NAME = "email";
  private static final String AUTH_PATH = "authentication";
  private static final String SESSION_PATH = "session";
  private static final String USER_ACCESSS_DICT = "user_asset_dictionary";
  private static final String USERNAME_QUERY_NAME = "username";
  private static final String KEY_QUERY_NAME = "key";
  private static final String USER_TOKEN_HEADER = "user_token";
  private static final String APP_ID_HEADER = "app_id";
  private static final String PASSWORD_TEMPLATE = "{\"value\":\"%s\"}";

  @NonNull
  private final String appId;

  private WebResource resource;

  public DefaultCUDClient(@NonNull ICGCClientConfig config) {
    super(config);
    checkStringArguments(config.getCudAppId(), config.getCudServiceUrl());
    setupClient();
    this.appId = config.getCudAppId();
    this.resource = jerseyClient.resource(config.getCudServiceUrl());
  }

  @Override
  public String login(String userName, String password) {
    checkStringArguments(userName, password);
    val response = resource
        .path(AUTH_PATH)
        .queryParam(USERNAME_QUERY_NAME, userName)
        .post(ClientResponse.class, format(PASSWORD_TEMPLATE, password));

    if (response.hasEntity()) {
      log.debug("Logged in.");
      return response.getEntity(JsonNode.class).get("token").asText();
    } else {
      throw new ICGCAccessException("Could not login");
    }
  }

  @Override
  public void logout(String token) {
    checkStringArguments(token);
    resource
        .path(SESSION_PATH)
        .path(token)
        .delete();
    log.debug("Logged out");
  }

  @Override
  public User getUserInfo(String authToken, String userToken) {
    checkStringArguments(authToken, userToken);
    return resource
        .path(SESSION_PATH)
        .path(userToken)
        .header(USER_TOKEN_HEADER, authToken)
        .get(ClientResponse.class)
        .getEntity(User.class);
  }

  @Override
  public void addItems(String token, String userName, @NonNull Map<String, String> items) {
    checkStringArguments(token, userName);
    checkArgument(!items.isEmpty(), "Empty items list");

    resource
        .path(USER_ACCESSS_DICT)
        .queryParam(EMAIL_QUERY_NAME, userName)
        .header(USER_TOKEN_HEADER, token)
        .post(new ItemsContainer(items));
  }

  @Override
  public void deleteItem(String token, String userName, String key) {
    checkStringArguments(token, userName, key);

    resource
        .path(USER_ACCESSS_DICT)
        .queryParam(EMAIL_QUERY_NAME, userName)
        .queryParam(KEY_QUERY_NAME, key)
        .header(USER_TOKEN_HEADER, token)
        .delete();
  }

  @Override
  public Map<String, String> getItem(String token, String userName, String key) {
    checkStringArguments(token, userName, key);

    val response = resource
        .path(USER_ACCESSS_DICT)
        .queryParam(EMAIL_QUERY_NAME, userName)
        .queryParam(KEY_QUERY_NAME, key)
        .header(USER_TOKEN_HEADER, token)
        .get(ClientResponse.class);

    if (response.hasEntity()) {
      return response.getEntity(Item.class).asMap();
    } else {
      throw new ICGCUnknownException("Response body is empty");
    }
  }

  private static ClientResponse checkResponse(ClientResponse response) {
    val status = response.getClientResponseStatus();

    if (status != Status.OK && status != Status.NO_CONTENT) {
      switch (status) {
      case INTERNAL_SERVER_ERROR:
        throw new NoSuchElementException(getErrorMessage(response, "Invalid key"));

      case UNAUTHORIZED:
        throw new ICGCAccessException(getErrorMessage(response, "Invalid application id"));

      case FORBIDDEN:
        // invalid token
        throw new ICGCAccessException(getErrorMessage(response, "Remote API permission denied"));

      case NOT_FOUND:
        throw new ICGCAccessException(getErrorMessage(response, "Remote API endpoint not found"));

      default:
        throw new ICGCUnknownException("An unknown error has occurred");
      }
    }

    return response;
  }

  private void setupClient() {
    jerseyClient.addFilter(new ClientFilter() {

      @Override
      public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        cr.getHeaders().putSingle(APP_ID_HEADER, appId);

        return getNext().handle(cr);
      }

    });

    jerseyClient.addFilter(new ClientFilter() {

      @Override
      public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        return checkResponse(getNext().handle(cr));
      }

    });
  }

}
