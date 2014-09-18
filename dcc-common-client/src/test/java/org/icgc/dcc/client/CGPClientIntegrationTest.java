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

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.icgc.client.api.ICGCAccessException;
import org.icgc.dcc.icgc.client.api.ICGCClient;
import org.icgc.dcc.icgc.client.api.ICGCClientConfig;
import org.icgc.dcc.icgc.client.api.ICGCUnknownException;
import org.icgc.dcc.icgc.client.api.cgp.CGPClient;
import org.icgc.dcc.icgc.client.api.cgp.CancerGenomeProject;
import org.icgc.dcc.icgc.client.api.cgp.DataLevelProject;
import org.icgc.dcc.icgc.client.api.cgp.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@Slf4j
public class CGPClientIntegrationTest {

  private static final String NO_API_URL = "http://***REMOVED***/ud_oauth/1/searc";
  private static final String SERVICE_URL = "http://***REMOVED***/ud_oauth/1/search";
  private static final String CONSUMER_KEY = "54CJyoWZc6LZ7prsrDAm5UeCmEE2VbDr_ck";
  private static final String WRONG_CONSUMER_KEY = "***REMOVED***";
  private static final String CONSUMER_SECRET = "C4umNCcesMRT6cmGYGYHEsNu5QNYGfok_cs";
  private static final String WRONG_CONSUMER_SECRET = "***REMOVED***";
  private static final String ACCESS_TOKEN = "PzrnixjR6rNcTiZeB5ZPBDEoDqidqL2D_at";
  private static final String WRONG_ACCESS_TOKEN = "***REMOVED***";
  private static final String ACCESS_SECRET = "9Lj5hf37irQ5RzSfruNrv3wt4QQqrfCr_as";

  private CGPClient client;

  @Rule
  public ExpectedException exception = ExpectedException.none();

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
    client = ICGCClient.create(config).cgp();
  }

  @Test
  public void consistencyTest() {
    val projects = client.getCancerGenomeProjects();

    for (val project : projects) {
      testCGP(project);
      testCGP(client.getCancerGenomeProject(project.getNid()));
      testCGP(client.details().getCancerGenomeProject(project.getNid()));
      testCGP(client.memberships().getCancerGenomeProject(project.getNid()));
      testCGPwithDLP(client.details().memberships().getCancerGenomeProject(project.getNid()));
    }
  }

  private static void testCGP(CancerGenomeProject cgp) {
    log.debug("Original {}", cgp);
    cgp.getNid();
    cgp.getName();
    cgp.getCountry();
    cgp.getOrganSystem();

    testDetails(cgp.getDetails());
    testUsers(cgp.getMembers());
    testUsers(cgp.getLeaders());
    testUsers(cgp.getDataSubmitters());
  }

  @Test
  public void consistencyByIdTest() {
    val id = "1172";
    testCGP(client.getCancerGenomeProject(id));
    testCGP(client.details().getCancerGenomeProject(id));
    testCGP(client.memberships().getCancerGenomeProject(id));
    testCGPwithDLP(client.details().memberships().getCancerGenomeProject(id));
  }

  private void testCGPwithDLP(CancerGenomeProject cgp) {
    log.debug("Original {}", cgp);
    cgp.getNid();
    cgp.getName();
    cgp.getCountry();
    cgp.getOrganSystem();

    testDetails(cgp.getDetails());
    testUsers(cgp.getMembers());
    testUsers(cgp.getLeaders());
    testUsers(cgp.getDataSubmitters());

    for (val dlp : cgp.getDlps()) {
      testDlp(dlp);
      testDlp(client.getDataLevelProject(dlp.getNid()));
      testDlp(client.details().getDataLevelProject(dlp.getNid()));
      testDlp(client.memberships().getDataLevelProject(dlp.getNid()));
      testDlp(client.details().memberships().getDataLevelProject(dlp.getNid()));
    }
  }

  private static void testDlp(DataLevelProject dlp) {
    dlp.getNid();
    dlp.getName();
    testUsers(dlp.getDataSubmitters());
    testUsers(dlp.getLeaders());
    testUsers(dlp.getMembers());
    testDetails(dlp.getDetails());
  }

  private static void testDetails(Map<String, String> entity) {
    for (val key : entity.keySet()) {
      entity.get(key);
    }
  }

  private static void testUsers(List<User> entity) {
    for (val user : entity) {
      user.getEmail();
      user.getUid();
    }
  }

  @Test(expected = NoSuchElementException.class)
  public void notFoundTest() {
    client.getCancerGenomeProject("00");
  }

  @Test
  public void noRestAPITest() {
    exception.expect(ICGCUnknownException.class);
    exception.expectMessage("REST API could not be found");
    val config = ICGCClientConfig.builder()
        .cgpServiceUrl(NO_API_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .build();
    val client = ICGCClient.create(config).cgp();
    client.getCancerGenomeProject("1172");
  }

  @Test
  public void wrongConsumerKeyTest() {
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Invalid consumer");
    val config = ICGCClientConfig.builder()
        .cgpServiceUrl(SERVICE_URL)
        .consumerKey(WRONG_CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .build();
    val client = ICGCClient.create(config).cgp();
    client.getCancerGenomeProject("1172");
  }

  @Test
  public void InvalidSignatureTest() {
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Invalid signature");
    val config = ICGCClientConfig.builder()
        .cgpServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(WRONG_CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .build();
    val client = ICGCClient.create(config).cgp();
    client.getCancerGenomeProject("1172");
  }

  @Test
  public void AccessTokenTest() {
    exception.expect(ICGCAccessException.class);
    exception.expectMessage("Invalid access token");
    val config = ICGCClientConfig.builder()
        .cgpServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(WRONG_ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .build();
    val client = ICGCClient.create(config).cgp();
    client.getCancerGenomeProject("1172");
  }

  @Test
  public void argumentsTest() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("Null or empty ID");
    client.getCancerGenomeProject("");
  }

  @Test
  @Ignore
  public void cgpResponseStructureTest() {
    // TODO how to check if CGPResponse - CancerGenomeProject mapping is correct?
  }

  @Test
  public void byIdTest() {
    printProject(client.details().getCancerGenomeProject("53049"));
  }

  private static void printProject(CancerGenomeProject proj) {
    System.out.printf("-----------------------------\n");
    System.out.printf("Id           : %s\n", proj.getNid());
    System.out.printf("Name         : %s\n", proj.getName());
    System.out.printf("Country      : %s\n", proj.getCountry());
    System.out.printf("Organ System : %s\n", proj.getOrganSystem());

    val details = proj.getDetails();
    for (val key : details.keySet()) {
      System.out.printf("%s : %s\n", key, details.get(key));
    }

    System.out.printf("\n---------DLPS--------\n");
    for (val dlp : proj.getDlps()) {
      printDlp(dlp);
    }
  }

  private static void printDlp(DataLevelProject dlp) {
    System.out.printf("\n======================\n");
    System.out.printf("Id : %s\n", dlp.getNid());
    System.out.printf("Name : %s\n", dlp.getName());
    val details = dlp.getDetails();
    for (val key : details.keySet()) {
      System.out.printf("%s : %s\n", key, details.get(key));
    }
  }

}
