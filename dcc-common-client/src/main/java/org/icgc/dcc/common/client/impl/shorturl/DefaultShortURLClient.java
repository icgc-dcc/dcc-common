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
package org.icgc.dcc.common.client.impl.shorturl;

import lombok.NonNull;
import lombok.val;

import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.shorturl.ShortURLClient;
import org.icgc.dcc.common.client.api.shorturl.ShortURLResponse;
import org.icgc.dcc.common.client.impl.BaseOAuthICGCClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class DefaultShortURLClient extends BaseOAuthICGCClient implements ShortURLClient {

  private static final String SHORTURL_PARAM_NAME = "longUrl";

  private final WebResource resourse;

  public DefaultShortURLClient(@NonNull ICGCClientConfig config) {
    super(config);
    checkStringArguments(config.getShortServiceUrl());
    this.resourse = jerseyClient.resource(config.getShortServiceUrl());
  }

  @Override
  public ShortURLResponse shorten(String url) {
    checkStringArguments(url);
    val clientResponse = resourse
        .queryParam(SHORTURL_PARAM_NAME, url)
        .get(ClientResponse.class);

    return clientResponse.getEntity(ShortURLResponse.class);
  }

}
