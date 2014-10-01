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
package org.icgc.dcc.common.client.api.daco;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A client to query DACO API. To get a DACO access it is required to fill-in the DACO application. For more information
 * check <a href="https://icgc.org/daco">How to get DACO access</a>.<br>
 * <br>
 * DACO access is granted to OpenID accounts only. In other words, if a user has an ICGC(CUD) account they also need an
 * OpenID account to get the DACO access. However, the DACO API does not check if the account is valid or not. Thus,
 * it's possible to use just a random unique URI.<br>
 * <br>
 * Multiple ICGC(CUD) accounts can share a single OpenID account. Also, they may be associated with more than one OpenID
 * account. Importantly, if the application associated to that OpenID is not approved, then the OpenID has no
 * authorization power into DACO. However, such a behaviour is unusual.
 */
public interface DACOClient {

  public enum UserType {
    OPENID("openid"),
    CUD("username");

    UserType(String value) {
      this.value = value;
    }

    private String value;

    @Override
    public String toString() {
      return value;
    }
  }

  /**
   * Gets all approved DACO users (OpenID list)
   * 
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773218">Search - All DACO Approved Users</a>
   */
  List<User> getUsers();

  /**
   * Gets approved DACO users by {@code openId}. As multiple accounts can share the same OpenID a list of users is
   * returned
   * 
   * @param openId - OpenID being searched
   * @throws NoSuchElementException
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773220">Search - One DACO Approved Users
   * (v2.0)</a>
   */
  List<User> getUser(String openId);

  /**
   * Returns filtered approved DACO users.
   * 
   * @param userType - type of search (by OpenID or by CUD)
   * @param userValue - OpenID or CUD being searched
   * @throws NoSuchElementException
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773222">Search - One DACO Approved Users by
   * entity-filter (v2.0)</a>
   */
  List<User> getUsersByType(UserType userType, String userValue);

  /**
   * Checks if {@code userId} is in the list of the approved DACO users. The {@code userId} may be either an
   * {@code OPENID} or a {@code CUD}.
   * 
   * @param userId to be checked. The user should pass user's {@code username} if the method is invoked for {@code CUD}
   * or user's openID in case of {@code OPENID}
   * @return <b>true</b> if the {@code userId} is approved, otherwise - <b>false</b>
   */
  boolean hasDacoAccess(String userId, UserType userType);

}
