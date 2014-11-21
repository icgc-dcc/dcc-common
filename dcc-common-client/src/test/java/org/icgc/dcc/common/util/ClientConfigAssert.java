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
package org.icgc.dcc.common.util;

import lombok.val;

import org.assertj.core.api.AbstractAssert;
import org.icgc.dcc.common.client.impl.BaseICGCClient;

public class ClientConfigAssert extends AbstractAssert<ClientConfigAssert, BaseICGCClient> {

  public ClientConfigAssert(BaseICGCClient actual) {
    super(actual, ClientConfigAssert.class);
  }

  public static ClientConfigAssert assertThat(BaseICGCClient actual) {
    return new ClientConfigAssert(actual);
  }

  public ClientConfigAssert toStringContainsFields(String... fieldNames) {
    // check that actual BaseICGCClient we want to make assertions on is not null.
    isNotNull();
    val toStringValue = actual.toString();

    for (val value : fieldNames) {
      if (!toStringValue.contains(value)) {
        failWithMessage("Expected that string representation of the client contains <%s> but was <%s>", value,
            toStringValue);
      }
    }

    return this;
  }

  public ClientConfigAssert toStringExcludesFields(String... fieldNames) {
    // check that actual BaseICGCClient we want to make assertions on is not null.
    isNotNull();
    val toStringValue = actual.toString();

    for (val value : fieldNames) {
      if (toStringValue.contains(value)) {
        failWithMessage("Expected that string representation of the client does not contain <%s> but was <%s>", value,
            toStringValue);
      }
    }

    return this;
  }

  public static void toStringIsCompleteAndProtected(BaseICGCClient client) {
    assertThat(client).toStringContainsFields("cgpServiceUrl", "cudServiceUrl", "shortServiceUrl", "consumerKey",
        "accessToken", "strictSSLCertificates", "requestLoggingEnabled");
    assertThat(client).toStringExcludesFields("consumerSecret", "accessSecret", "cudAppId", "jerseyClient");
  }

}
