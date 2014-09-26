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
package org.icgc.dcc.common.client.impl.daco;

import static java.lang.String.format;
import static org.icgc.dcc.common.client.api.daco.DACOClient.UserType.OPENID;
import static org.icgc.dcc.common.client.api.daco.DACOClient.UserType.CUD;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.val;

import org.icgc.dcc.common.client.api.ICGCClientConfig;
import org.icgc.dcc.common.client.api.daco.DACOClient;
import org.icgc.dcc.common.client.api.daco.User;
import org.icgc.dcc.common.client.impl.BaseOAuthICGCClient;

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
  public List<User> getUser(String openId) {
    checkStringArguments(openId);
    val clientResponse = resourse
        .queryParam(ENTITY_ID_PARAM_NAME, openId)
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
  public List<User> getUsersByType(@NonNull UserType userType, String userValue) {
    checkStringArguments(userValue);
    val clientResponse = resourse
        .queryParam(ENTITY_FILTER_PARAM_NAME, getFilter(userType, userValue))
        .get(ClientResponse.class);

    return convert(clientResponse.getEntity(new GenericType<List<ResponseUser>>() {}));
  }

  @Override
  public boolean hasDacoAccess(String userId, @NonNull UserType userType) {
    checkStringArguments(userId);

    return userType == OPENID ? hasDacoAccessByOpenid(userId) : hasDacoAccessByUsername(userId);
  }

  private boolean hasDacoAccessByOpenid(String openId) {
    for (val user : getUsers()) {
      if (user.getOpenid().equals(openId)) {
        return true;
      }
    }

    return false;
  }

  private boolean hasDacoAccessByUsername(String userName) {
    List<User> result = Collections.emptyList();
    try {
      result = getUsersByType(CUD, userName);
    } catch (NoSuchElementException e) {
      // BaseOAuthICGCClient throws NoSuchElementException if receives 204 "No Content" response from the ICGC API.
    }

    return result.isEmpty() ? false : true;
  }

  private static String getFilter(UserType userType, String userValue) {
    return format(FILTER_TEMPLATE, userType, userValue);
  }

}
