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
package org.icgc.dcc.icgc.client.impl.daco;

import static java.lang.String.format;
import static org.icgc.dcc.icgc.client.api.daco.DACOClient.FilterType.OPENID;
import static org.icgc.dcc.icgc.client.api.daco.DACOClient.FilterType.USERNAME;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.val;

import org.icgc.dcc.icgc.client.api.ICGCClientConfig;
import org.icgc.dcc.icgc.client.api.daco.DACOClient;
import org.icgc.dcc.icgc.client.api.daco.User;
import org.icgc.dcc.icgc.client.impl.BaseOAuthICGCClient;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class DefaultDACOClient extends BaseOAuthICGCClient implements DACOClient {

  private static final String ENTITY_ID_PARAM_NAME = "entity-id";
  private static final String ENTITY_FILTER_PARAM_NAME = "entity-filter";
  private static final String ENTITY_TYPE_PARAM_NAME = "entity-type";
  private static final String ENTITY_TYPE_PARAM_VALUE = "daco";
  private static final String FILTER_TEMPLATE = "{\"%s\":\"%s\"}";

  private final WebResource resourse;

  public DefaultDACOClient(@NonNull ICGCClientConfig config) {
    super(config);
    checkStringArguments(config.getCgpServiceUrl());
    this.resourse = jerseyClient
        .resource(config.getCgpServiceUrl())
        .queryParam(ENTITY_TYPE_PARAM_NAME, ENTITY_TYPE_PARAM_VALUE);
  }

  @Override
  public List<User> getUsers() {
    return convert(resourse.get(ClientResponse.class).getEntity(new GenericType<List<ResponseUser>>() {}));
  }

  private static List<User> convert(List<ResponseUser> source) {
    val result = new ImmutableList.Builder<User>();
    for (val user : source) {
      result.add(User.builder().openid(user.getOpenid()).build());
    }

    return result.build();
  }

  @Override
  public List<User> getUser(String id) {
    checkStringArguments(id);
    val clientResponse = resourse
        .queryParam(ENTITY_ID_PARAM_NAME, id)
        .get(ClientResponse.class);

    return convert(clientResponse.getEntity(UserContainer.class));
  }

  private static List<User> convert(UserContainer source) {
    val result = new ImmutableList.Builder<User>();
    for (val user : source.getUser().getUserinfo()) {
      result.add(User.builder()
          .openid(source.getUser().getOpenid())
          .username(user.getUid())
          .name(user.getName())
          .email(user.getEmail())
          .build());
    }

    return result.build();
  }

  @Override
  public List<User> getFilteredUsers(@NonNull FilterType filterType, String filterValue) {
    checkStringArguments(filterValue);
    val clientResponse = resourse
        .queryParam(ENTITY_FILTER_PARAM_NAME, getFilter(filterType, filterValue))
        .get(ClientResponse.class);

    return convert(clientResponse.getEntity(new GenericType<List<ResponseUser>>() {}));
  }

  @Override
  public boolean hasDacoAccess(String id, @NonNull FilterType idType) {
    checkStringArguments(id);

    return idType == OPENID ? hasDacoAccessByOpenid(id) : hasDacoAccessByUsername(id);
  }

  private boolean hasDacoAccessByOpenid(String openId) {
    for (val user : getUsers()) {
      if (user.getOpenid().equals(openId)) {
        return true;
      }
    }

    return false;
  }

  private boolean hasDacoAccessByUsername(String username) {
    List<User> result = Collections.emptyList();
    try {
      result = getFilteredUsers(USERNAME, username);
    } catch (NoSuchElementException e) {
      // BaseOAuthICGCClient throws NoSuchElementException if receives 204 "No Content" response from the ICGC API.
    }

    return result.isEmpty() ? false : true;
  }

  private static String getFilter(FilterType filterType, String filterValue) {
    return format(FILTER_TEMPLATE, filterType, filterValue);
  }

}
