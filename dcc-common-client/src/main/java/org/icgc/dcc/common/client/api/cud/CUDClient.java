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
package org.icgc.dcc.common.client.api.cud;

import java.util.Map;
import java.util.NoSuchElementException;

import org.icgc.dcc.common.client.api.ICGCAccessException;

/**
 * A client that can query CUD API
 * 
 * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD+APIs+v1.0">CUD APIs v1.0</a>
 */
public interface CUDClient {

  /**
   * Authenticates {@code userName} with CUD API. I.e. logins {@code userName} to the CUD API.<br>
   * After the operation a new session is established with unknown duration. However, it is more than one hour long.<br>
   * The {@code userName} is assigned a session token that can be used with other method of this client that require an
   * authentication token.
   * 
   * @param userName of the CUD account performing actions against the CUD provider service.
   * @param password of the {@code userName}
   * @return {@code session token} that is used for API client authorizations
   * @throws ICGCAccessException when the {@code cudAppId}, {@code userName} or {@code password} is invalid
   * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD-LOGIN">CUD Login</a>
   */
  String login(String userName, String password);

  /**
   * Deletes the token received on login to logout.
   * 
   * @throws ICGCAccessException when the {@code cudAppId} is invalid
   * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD-LOGOUT">CUD Logout</a>
   */
  void logout(String token);

  /**
   * Adds the {@code items} to {@code userName} directory. If there are items with the same key in the {@code items}
   * collection, value of the last one will be final.
   * 
   * @param token used for user authentication
   * @param userName - user's dictionary to be used
   * @param items to be stored
   * @throws ICGCAccessException when the {@code cudAppId}/{@code token} is invalid/expired or the {@code userName} does
   * not have permission to access the API
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=54690293">CUD add/update User Asset
   * Dictionary</a>
   */
  void addItems(String token, String userName, Map<String, String> items);

  /**
   * Deletes an item from {@code userName's} directory.
   * 
   * @param token used for user authentication
   * @param userName - user's dictionary to be used
   * @param key of the item to be deleted
   * @throws NoSuchElementException
   * @throws ICGCAccessException when the{@code cudAppId}/{@code token} is invalid/expired or the {@code userName} does
   * not have permission to access the API
   * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD-DELETE+USER_ASSET_DICTIONARY">CUD Delete</a>
   */
  void deleteItem(String token, String userName, String key);

  /**
   * Gets an item from {@code userName's} directory.
   * 
   * @param token used for user authentication
   * @param userName - user's dictionary to be used
   * @param key of the item to be retrieved
   * @return key/value pair of the retrieved item. The map contains only one element
   * @throws ICGCAccessException when the {@code cudAppId}/{@code token} is invalid/expired or the {@code userName} does
   * not have permission to access the API
   * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD-READ+USER_ASSET_DICTIONARY">CUD Read User Asset
   * Dictionary</a>
   */
  Map<String, String> getItem(String token, String userName, String key);

  /**
   * Gets user information.
   * 
   * @param authToken - user's token used for authorization
   * @param userToken - identifies user whose information will be returned
   * @throws NoSuchElementException
   * @throws ICGCAccessException when the {@code cudAppId}, {@code authToken} or the {@code userToken} is invalid or
   * expired
   * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/CUD-READ+USER+INFORMATION"> CUD Read User Information</a>
   */
  User getUserInfo(String authToken, String userToken);

}
