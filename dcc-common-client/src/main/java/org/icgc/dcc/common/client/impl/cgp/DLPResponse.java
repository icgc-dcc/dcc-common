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
package org.icgc.dcc.common.client.impl.cgp;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Value;
import lombok.val;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableMap;

@Value
public class DLPResponse {

  String nid;
  String name;
  List<ItemContainer> details;
  MembershipsContainer memberships;
  @Getter(lazy = true)
  private final Map<String, String> dlpDetails = createDlpDetails();

  @JsonCreator
  public DLPResponse(
      @JsonProperty("nid") String nid,
      @JsonProperty("name") String name,
      @JsonProperty("details") List<ItemContainer> details,
      @JsonProperty("memberships") MembershipsContainer memberships)
  {
    this.nid = nid;
    this.name = name;
    this.details = details;
    this.memberships = memberships;
  }

  private Map<String, String> createDlpDetails() {
    val result = new ImmutableMap.Builder<String, String>();
    if (details == null) return result.build();

    for (val container : details) {
      if (container.getItem().getValue() != null) {
        result.put(container.getItem().getKey(), container.getItem().getValue());
      }
    }

    return result.build();
  }

}
