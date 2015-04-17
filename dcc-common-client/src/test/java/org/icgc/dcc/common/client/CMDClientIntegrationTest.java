/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
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

import org.icgc.dcc.common.client.api.ICGCClient;
import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.ICGCEntityNotFoundException;
import org.icgc.dcc.common.client.api.cms.CMSClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CMDClientIntegrationTest {

  private static final String BETATRA_SESSION_NAME = "SESS10272bceecb84d3d5608ae4b0a4e5934";
  private static final String SERVICE_URL = "https://***REMOVED***/cms_oauth_coop/1";
  private static final String CONSUMER_KEY = "***REMOVED***";
  private static final String CONSUMER_SECRET = "***REMOVED***";
  private static final String ACCESS_TOKEN = "***REMOVED***";
  private static final String ACCESS_SECRET = "***REMOVED***";

  CMSClient client;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() {
    client = ICGCClient.create(createConfig()).cms();
  }

  @Test
  public void sessionNameTest() {
    assertThat(client.getSessionName()).isEqualTo(BETATRA_SESSION_NAME);
  }

  // TODO: find a method to authenticate
  @Test
  public void sessionInfoTest() {
    assertThat(client.getUserInfo("A CORRECT SESSION ID HERE")).isNotNull();
  }

  @Test
  public void wrongSessionInfoTest() {
    thrown.equals(ICGCEntityNotFoundException.class);
    thrown.expectMessage("[404] The session not found");
    client.getUserInfo("fake");
  }

  private static ICGCClientConfig createConfig() {
    return ICGCClientConfig.builder()
        .cgpServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .requestLoggingEnabled(true)
        .strictSSLCertificates(false)
        .build();
  }

}
