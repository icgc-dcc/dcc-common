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
package org.icgc.dcc.common.client.api.daco;

import java.util.List;

import lombok.Value;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * An actual response from the DACO API is represented by this model.
 */
@Value
public class DACOUser {

  @Value
  public static class Userinfo {

    @JsonCreator
    public Userinfo(
        @JsonProperty("uid") String uid,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email)
    {
      this.uid = uid;
      this.name = name;
      this.email = email;
    }

    String uid;
    String name;
    String email;
  }

  String openid;
  boolean cloudAccess;
  List<Userinfo> userinfo;

  @JsonCreator
  public DACOUser(
      @JsonProperty("openid") String openid,
      @JsonProperty("csa") boolean cloudAccess,
      @JsonProperty("userinfo") List<Userinfo> userinfo)
  {
    this.openid = openid;
    this.cloudAccess = cloudAccess;
    this.userinfo = userinfo;
  }

}
