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
package org.icgc.dcc.common.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icgc.dcc.common.util.ClientConfigAssert.assertThat;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.common.client.api.ICGCAccessException;
import org.icgc.dcc.common.client.api.ICGCClient;
import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.ICGCUnknownException;
import org.icgc.dcc.common.client.api.shorturl.ShortURLClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@Slf4j
public class ShortUrlClientIntegrationTest {

  private static final String NO_API_URL = "http://***REMOVED***/ud_oauth/1/searc";
  private static final String SERVICE_URL = "https://***REMOVED***/short/api/1/shorten";
  private static final String CONSUMER_KEY = "***REMOVED***";
  private static final String WRONG_CONSUMER_KEY = "***REMOVED***";
  private static final String CONSUMER_SECRET = "***REMOVED***";
  private static final String WRONG_CONSUMER_SECRET = "***REMOVED***";
  private static final String ACCESS_TOKEN = "***REMOVED***";
  private static final String WRONG_ACCESS_TOKEN = "***REMOVED***";
  private static final String ACCESS_SECRET = "***REMOVED***";
  private static final String SAMPLE_URL = "http://docs.oracle.com/javase/7/docs/api/";
  private static final String URL_REGEX = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,4})/[a-zA-Z]{3,}$";

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private ShortURLClient client;

  @Before
  public void setUp() {
    val config = ICGCClientConfig.builder()
        .shortServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .strictSSLCertificates(false)
        .requestLoggingEnabled(true)
        .build();
    client = ICGCClient.create(config).shortUrl();
  }

  @Test
  public void successfulTest() {
    assertThat(client.shorten(SAMPLE_URL).getShortUrl()).matches(URL_REGEX);
  }

  @Test
  public void incorrectUrlTest() {
    // TODO returns 200 OK what seems to be incorrect
    log.debug("{}", client.shorten("http"));
  }

  @Test
  public void incorrectServiceUrlTest() {
    exception.expect(ICGCUnknownException.class);
    exception.expectMessage("REST API could not be found");
    val config = ICGCClientConfig.builder()
        .shortServiceUrl(NO_API_URL)
        .consumerKey(WRONG_CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .requestLoggingEnabled(true)
        .build();
    val client = ICGCClient.create(config).shortUrl();
    client.shorten(SAMPLE_URL);
  }

  @Test
  public void consumerKeyTest() {
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Invalid consumer");
    val config = ICGCClientConfig.builder()
        .shortServiceUrl(SERVICE_URL)
        .consumerKey(WRONG_CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .strictSSLCertificates(false)
        .requestLoggingEnabled(true)
        .build();
    val client = ICGCClient.create(config).shortUrl();
    client.shorten(SAMPLE_URL);
  }

  @Test
  public void signatureTest() {
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Invalid signature");
    val config = ICGCClientConfig.builder()
        .shortServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(WRONG_CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .strictSSLCertificates(false)
        .requestLoggingEnabled(true)
        .build();
    val client = ICGCClient.create(config).shortUrl();
    client.shorten(SAMPLE_URL);
  }

  @Test
  public void accessTokenTest() {
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Invalid access token");
    val config = ICGCClientConfig.builder()
        .shortServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(WRONG_ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .strictSSLCertificates(false)
        .requestLoggingEnabled(true)
        .build();
    val client = ICGCClient.create(config).shortUrl();
    client.shorten(SAMPLE_URL);
  }

  @Test
  public void toStringTest() {
    assertThat(client).toStringIsCompleteAndProtected();
  }

}
