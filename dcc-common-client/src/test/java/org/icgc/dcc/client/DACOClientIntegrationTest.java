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
package org.icgc.dcc.client;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.NoSuchElementException;

import lombok.val;

import org.icgc.dcc.icgc.client.api.ICGCClient;
import org.icgc.dcc.icgc.client.api.ICGCClientConfig;
import org.icgc.dcc.icgc.client.api.daco.DACOClient;
import org.icgc.dcc.icgc.client.api.daco.DACOClient.FilterType;
import org.junit.Before;
import org.junit.Test;

public class DACOClientIntegrationTest {

  private static final String EMAIL_REGEX = "\\w+[\\w\\.]*@\\w*\\.\\w{2,4}";
  private static final String NOT_FOUND_MESSAGE = "An entity with such ID was not found";
  private static final String VALID_OPENID = "Jorgereisfilho01@gmail.com";
  private static final String SERVICE_URL = "http://***REMOVED***/ud_oauth/1/search";
  private static final String CONSUMER_KEY = "54CJyoWZc6LZ7prsrDAm5UeCmEE2VbDr_ck";
  private static final String CONSUMER_SECRET = "C4umNCcesMRT6cmGYGYHEsNu5QNYGfok_cs";
  private static final String ACCESS_TOKEN = "PzrnixjR6rNcTiZeB5ZPBDEoDqidqL2D_at";
  private static final String ACCESS_SECRET = "9Lj5hf37irQ5RzSfruNrv3wt4QQqrfCr_as";

  private DACOClient client;

  @Before
  public void setUp() {
    val config = ICGCClientConfig.builder()
        .cgpServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .requestLoggingEnabled(true)
        .build();
    client = ICGCClient.create(config).daco();
  }

  @Test
  public void getUsersTest() {
    assertThat(client.getUsers()).isNotEmpty();
  }

  @Test
  public void getUserTest() {
    for (val user : client.getUser(VALID_OPENID)) {
      assertThat(user.getOpenid()).isNotEmpty();
      assertThat(user.getEmail()).matches(EMAIL_REGEX);
      assertThat(user.getName()).isNotEmpty();
      assertThat(user.getUid()).matches("\\d+");
    }
    catchException(client).getUser("fake");
    assertNoSuchElementException(caughtException());
  }

  @Test
  public void getFilteredUsersTest() {
    client.getFilteredUsers(FilterType.OPENID, VALID_OPENID);

    catchException(client).getFilteredUsers(FilterType.OPENID, "fake");
    assertNoSuchElementException(caughtException());

    catchException(client).getFilteredUsers(FilterType.USERNAME, "fake");
    assertNoSuchElementException(caughtException());
    // TODO find username that matches
  }

  @Test
  public void hasDacoAccessTest() {
    assertThat(client.hasDacoAccess(VALID_OPENID)).isTrue();
    assertThat(client.hasDacoAccess("SomeInvalidOpenId@gmail.com")).isFalse();
  }

  private static void assertNoSuchElementException(Exception e) {
    assertThat(e)
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(NOT_FOUND_MESSAGE);
  }

}
