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
package org.icgc.dcc.icgc.client.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.icgc.dcc.icgc.client.api.ICGCClientConfig;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * Base class to initialize a ICGC REST API client
 */
@Slf4j
public abstract class BaseICGCClient {

  protected final ICGCClientConfig config;
  protected final Client jerseyClient;

  public BaseICGCClient(@NonNull ICGCClientConfig config) {
    this.config = config;
    this.jerseyClient = Client.create(getClientConfig());
    configureFilters();
    configureLogging();
  }

  private void configureFilters() {
    jerseyClient.addFilter(new ClientFilter() {

      @Override
      public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        val headers = cr.getHeaders();
        headers.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        headers.putSingle(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        return getNext().handle(cr);
      }

    });
  }

  private void configureLogging() {
    if (config.isRequestLoggingEnabled()) {
      log.debug("Enabling HTTP requests/responses logging.");
      jerseyClient.addFilter(new LoggingFilter());
    }
  }

  private ClientConfig getClientConfig() {
    ClientConfig cc = new DefaultClientConfig();
    cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    cc.getClasses().add(JacksonJsonProvider.class);

    return configureSSLCertificatesHandling(cc);
  }

  @SneakyThrows
  private ClientConfig configureSSLCertificatesHandling(ClientConfig config) {
    if (!this.config.isStrictSSLCertificates()) {
      log.debug("Setting up SSL context");
      val context = SSLContext.getInstance("TLS");
      context.init(null, new TrustManager[] {
          new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
              return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

          } },
          null);

      config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
          new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
              return true;
            }

          }, context
          ));
    }

    return config;
  }

  /**
   * Checks if <code>arguments</code> array does not contain empty or null strings.
   * 
   * @param arguments to be checked.
   * @throws IllegalArgumentException
   */
  protected static void checkStringArguments(String... arguments) {
    for (val argument : arguments) {
      checkArgument(!isNullOrEmpty(argument), "Null or empty argument");
    }
  }

  /**
   * Gets body from <code>response</code> if available
   * 
   * @param response - source of the body
   * @param defaultMessage - returned in case the <code>resopnse</code> does not have body
   */
  protected static String getErrorMessage(ClientResponse response, String defaultMessage) {
    return response.hasEntity() ? response.getEntity(String.class) : defaultMessage;
  }

}
