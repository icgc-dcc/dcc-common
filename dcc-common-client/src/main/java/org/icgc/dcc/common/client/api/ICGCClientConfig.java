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
package org.icgc.dcc.common.client.api;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.val;

/**
 * A container of configuration required to interact with the ICGC API.
 * <p>
 * It has following configuration parameters that are set using corresponding setter methods of the
 * {@link ICGCClientConfigBuilder}. It is not required to set all the parameters. Set only those ones that are mandatory
 * for the API client which will be used.
 * </p>
 * 
 * @param cgpServiceUrl - CGP API endpoint.
 * @param cudServiceUrl - CUD API endpoint.
 * @param shortServiceUrl - ShortenURL API endpoint.
 * <p>
 * @param cudAppId - A public key required in each message to a CUD provider service.
 * @param consumerKey - Identifies which application is making the request.
 * @param consumerSecret - a sercret for <code>consumerKey</code>
 * @param accessToken - Represents a user's permission to share access to their account with your application.
 * @param accessSecret - a sercret for <code>accessToken</code>
 * </p>
 * @param strictSSLCertificates - Trust self-signed SSL certificates?<br>
 * <ul>
 * <li><code>true</code> - do not trust.
 * <li><code>false</code> - trust.<br>
 * <li>Default: <code>true</code>.
 * </ul>
 * @param requestLoggingEnabled - Enables logging of HTTP requests/responses. <b>Default:</b> <code>false</code>
 * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/ICGC.org+APIs+v2.0">ICGC.org APIs v2.0</a>
 * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD+APIs+v1.0">CUD APIs v1.0</a>
 * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/Shorten">Shorten API</a>
 * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/About+OAuth+1.0a+-+Authenticated+Requests">OAuth 1.0a</a>
 */
@Value
@Builder
@ToString(exclude = { "consumerSecret", "accessSecret", "cudAppId" })
public class ICGCClientConfig {

  private String cgpServiceUrl;
  private String cudServiceUrl;
  private String shortServiceUrl;
  private String cmsServiceUrl;
  private String consumerKey;
  private String consumerSecret;
  private String accessToken;
  private String accessSecret;
  private String cudAppId;
  private boolean strictSSLCertificates;
  private boolean requestLoggingEnabled;

  public static ICGCClientConfigBuilder builder() {
    val builder = new ICGCClientConfigBuilder();
    builder.strictSSLCertificates(true);

    return builder;
  }

}
