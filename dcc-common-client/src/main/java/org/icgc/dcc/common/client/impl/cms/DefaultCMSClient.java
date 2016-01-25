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
package org.icgc.dcc.common.client.impl.cms;

import lombok.NonNull;

import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.cms.CMSClient;
import org.icgc.dcc.common.client.api.cud.User;
import org.icgc.dcc.common.client.impl.BaseOAuthICGCClient;

import com.sun.jersey.api.client.WebResource;

public class DefaultCMSClient extends BaseOAuthICGCClient implements CMSClient {

  private static final String SESSION_NAME_ENDPOINT = "session_name";
  private static final String USER_INFO_ENDPOINT = "session";

  private final WebResource resourse;

  public DefaultCMSClient(ICGCClientConfig config) {
    super(config);
    this.resourse = jerseyClient.resource(config.getCmsServiceUrl());
  }

  @Override
  public String getSessionName() {
    return resourse.path(SESSION_NAME_ENDPOINT)
        .get(CmsResponse.class)
        .getName();
  }

  @Override
  public User getUserInfo(@NonNull String session) {
    return resourse.path(USER_INFO_ENDPOINT)
        .path(session)
        .get(User.class);
  }

}
