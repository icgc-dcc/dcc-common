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
package org.icgc.dcc.common.client.impl;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.common.client.api.ICGCAccessException;
import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.ICGCEntityNotFoundException;
import org.icgc.dcc.common.client.api.ICGCUnknownException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

@Slf4j
public abstract class BaseOAuthICGCClient extends BaseICGCClient {

  private static final String CMS_NOT_FOUND_RESPONSE = "{\"message\":\"Invalid token\"}";
  private static final String SIGNATURE_METHOD = "HMAC-SHA1";

  public BaseOAuthICGCClient(@NonNull ICGCClientConfig config) {
    super(config);
    addOAuthFilter();
    addCheckResponseFilter();
  }

  private void addOAuthFilter() {
    checkStringArguments(config.getConsumerKey(), config.getConsumerSecret(), config.getAccessToken(),
        config.getAccessSecret());
    log.debug("Adding oauth filter");
    val params = new OAuthParameters()
        .signatureMethod(SIGNATURE_METHOD)
        .consumerKey(config.getConsumerKey())
        .token(config.getAccessToken());
    val secrets = new OAuthSecrets()
        .consumerSecret(config.getConsumerSecret())
        .tokenSecret(config.getAccessSecret());
    jerseyClient.addFilter(new OAuthClientFilter(jerseyClient.getProviders(), params, secrets));
  }

  private void addCheckResponseFilter() {
    jerseyClient.addFilter(new ClientFilter() {

      @Override
      public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        return checkResponse(getNext().handle(cr));
      }

    });
  }

  /**
   * Check if response is successful. Otherwise, throws an exception based on cause.
   */
  private static ClientResponse checkResponse(ClientResponse response) {
    val status = response.getClientResponseStatus();
    if (status != Status.OK) {
      switch (status) {
      case NO_CONTENT:
        throw new ICGCEntityNotFoundException("An entity with such ID was not found");

      case NOT_FOUND:
        if (isCmsApiNotFound(response)) {
          throw new ICGCEntityNotFoundException("[404] The session not found");
        }

        throw new ICGCUnknownException("[404] Remote API endpoint not found");

      case UNAUTHORIZED:
        throw new ICGCAccessException(getErrorMessage(response, "Not authorized"));

      case FORBIDDEN:
        throw new ICGCAccessException(getErrorMessage(response, "Could not access the API"));

      default:
        throw new ICGCUnknownException(getErrorMessage(response, "An unknown error has occurred"));
      }
    }

    return response;
  }

  private static boolean isCmsApiNotFound(ClientResponse response) {
    val entity = response.getEntity(String.class);
    if (entity != null) {
      return entity.equals(CMS_NOT_FOUND_RESPONSE);
    }

    return false;
  }

}
