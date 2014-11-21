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

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.icgc.dcc.common.util.ClientConfigAssert.assertThat;

import java.util.Map;
import java.util.NoSuchElementException;

import lombok.val;

import org.icgc.dcc.common.client.api.ICGCAccessException;
import org.icgc.dcc.common.client.api.ICGCClient;
import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.cud.CUDClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;

public class CUDClientIntegrationTest {

  private static final String WRONG_SERVICE_URL = "https://***REMOVED***/ud_res";
  private static final String CUD_SERVICE_URL = "https://***REMOVED***/ud_rest/1";
  private static final String APP_ID = System.getProperty("app_id");
  private static final String CUD_USER = "***REMOVED***";
  private static final String CUD_USER_2 = "someuser@o.ca";
  private static final String CUD_PASSWD = "***REMOVED***";
  private static final String WRONG_TOKEN = "~";
  private static final String INVALID_KEY_MESSAGE = "{\"message\":\"Invalid key\"}";
  private static final String INVALID_TOKEN_MESSAGE = "{\"message\":\"Invalid token\"}";
  private static final String PERMISSION_DENIED_MESSAGE = "REST API permission deny";

  private static CUDClient client;
  private static final Map<String, String> singleItem = createOneItem();
  private static final Map<String, String> multiItems = createMultiItems();
  private String token;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() {
    val config = ICGCClientConfig.builder()
        .cudServiceUrl(CUD_SERVICE_URL)
        .cudAppId(APP_ID)
        .strictSSLCertificates(false)
        .requestLoggingEnabled(true)
        .build();
    client = ICGCClient
        .create(config)
        .cud();
    token = client.login(CUD_USER, CUD_PASSWD);
  }

  @Test
  public void serviceUrlTest() {
    val config = ICGCClientConfig.builder().
        cudServiceUrl(WRONG_SERVICE_URL).
        cudAppId(APP_ID).
        strictSSLCertificates(false).
        requestLoggingEnabled(true).
        build();
    val client = ICGCClient.create(config).cud();
    exception.expect(ICGCAccessException.class);
    client.login(CUD_USER, CUD_PASSWD);
  }

  @Test
  public void usernameTest() {
    val config = ICGCClientConfig.builder().
        cudServiceUrl(CUD_SERVICE_URL).
        cudAppId(APP_ID).
        strictSSLCertificates(false).
        requestLoggingEnabled(true).
        build();
    val client = ICGCClient.create(config).cud();

    exception.expect(ICGCAccessException.class);
    exception.expectMessage("authentication failed. please try webpage login to find more information.");
    client.login("zzz", CUD_PASSWD);
  }

  @Test
  public void userPasswordTest() {
    val config = ICGCClientConfig.builder().
        cudServiceUrl(CUD_SERVICE_URL).
        cudAppId(APP_ID).
        strictSSLCertificates(false).
        requestLoggingEnabled(true).
        build();
    val client = ICGCClient.create(config).cud();

    exception.expect(ICGCAccessException.class);
    exception.expectMessage("authentication failed. please try webpage login to find more information.");
    client.login(CUD_USER, "zzz");
  }

  @Test
  public void tokenTest() {
    catchException(client).deleteItem(WRONG_TOKEN, CUD_USER, "somekey");
    assertPermissionDeniedException(caughtException());

    catchException(client).addItems(WRONG_TOKEN, CUD_USER, singleItem);
    assertPermissionDeniedException(caughtException());

    catchException(client).getItem(WRONG_TOKEN, CUD_USER, "somekey");
    assertPermissionDeniedException(caughtException());

    catchException(client).getUserInfo(token, WRONG_TOKEN);
    assertThat(caughtException())
        .isInstanceOf(ICGCAccessException.class)
        .hasMessage(INVALID_TOKEN_MESSAGE);
  }

  @Test
  public void appIdTest() {
    val config = ICGCClientConfig.builder().
        cudServiceUrl(CUD_SERVICE_URL).
        cudAppId("2341234").
        strictSSLCertificates(false).
        requestLoggingEnabled(true).
        build();
    val client = ICGCClient.create(config).cud();
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Application failed to authenticate");
    client.login(CUD_USER, CUD_PASSWD);
  }

  @Test
  public void getUserInfoTest() {
    val user = client.getUserInfo(token, token);
    assertThat(user.getUserName()).isNotEmpty();
    assertThat(user.getEmail()).isNotEmpty();
    assertThat(user.getFirstName()).isNotEmpty();
    assertThat(user.getLastName()).isNotEmpty();
  }

  @Test
  public void addItemsTest() {
    client.addItems(token, CUD_USER, multiItems);
    client.addItems(token, CUD_USER_2, multiItems);
  }

  @Test
  public void deleteItemTest() {
    String key = insertItem(CUD_USER).keySet().iterator().next();
    client.deleteItem(token, CUD_USER, key);

    key = insertItem(CUD_USER_2).keySet().iterator().next();
    client.deleteItem(token, CUD_USER_2, key);

    catchException(client).deleteItem(token, CUD_USER, "~");
    assertInvalidKeyException(caughtException());

    catchException(client).deleteItem(token, CUD_USER_2, "~");
    assertInvalidKeyException(caughtException());
  }

  @Test
  public void getItemTest() {
    getItemTestHelper(CUD_USER);
    getItemTestHelper(CUD_USER_2);

    catchException(client).getItem(token, CUD_USER, "fake");
    assertInvalidKeyException(caughtException());

    catchException(client).getItem(token, CUD_USER_2, "fake");
    assertInvalidKeyException(caughtException());

    catchException(client).getItem(token, "fake", "fake");
    assertInvalidKeyException(caughtException());
  }

  private static Map<String, String> createOneItem() {
    return ImmutableMap.of("key1", "value1");
  }

  private static Map<String, String> createMultiItems() {
    return ImmutableMap.of("key1", "value", "key2", "value");
  }

  private Map<String, String> insertItem(String username) {
    client.addItems(token, username, singleItem);

    return singleItem;
  }

  private void getItemTestHelper(String username) {
    Map<String, String> map = insertItem(username);
    String key = map.keySet().iterator().next();
    String value = map.get(key);
    assertThat(client.getItem(token, username, key))
        .isInstanceOf(Map.class)
        .containsKey(key)
        .containsValue(value)
        .hasSize(1);
  }

  private void assertInvalidKeyException(Exception e) {
    assertThat(e)
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(INVALID_KEY_MESSAGE);
  }

  private void assertPermissionDeniedException(Exception e) {
    assertThat(e)
        .isInstanceOf(ICGCAccessException.class)
        .hasMessage(PERMISSION_DENIED_MESSAGE);
  }

  @Test
  public void toStringTest() {
    assertThat(client).toStringIsCompleteAndProtected();
  }

}
