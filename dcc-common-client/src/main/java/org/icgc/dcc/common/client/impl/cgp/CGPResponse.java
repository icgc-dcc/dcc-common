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
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.val;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * CGP API response. For internal use only. Must be converted to CancerReponseProject before returned back.
 */
@Value
@ToString(exclude = { "cgpDetails", "dlpDetails" })
public class CGPResponse {

  @Value
  public static class CGP {

    String nid;
    String name;
    String organSystem;
    String country;

    @JsonCreator
    public CGP(
        @NonNull @JsonProperty("nid") String nid,
        @JsonProperty("name") String name,
        @JsonProperty("organ_system") String organSystem,
        @JsonProperty("country") String country)
    {
      this.nid = nid;
      this.name = name;
      this.organSystem = organSystem;
      this.country = country;
    }
  }

  @Value
  public static class DLPContainer {

    @Value
    public static class DLP {

      String nid;
      String name;

      @JsonCreator
      public DLP(
          @JsonProperty("nid") String nid,
          @JsonProperty("name") String name)
      {
        this.nid = nid;
        this.name = name;
      }
    }

    DLP dlp;

    @JsonCreator
    public DLPContainer(@JsonProperty("dlp") DLP dlp) {
      this.dlp = dlp;
    }
  }

  @Value
  public static class Details {

    @Value
    public static class DLPDetailsContainer {

      @Value
      public static class DLP {

        List<ItemContainer> details;

        @JsonCreator
        public DLP(@JsonProperty("details") List<ItemContainer> details) {
          this.details = details;
        }
      }

      DLP dlp;

      @JsonCreator
      public DLPDetailsContainer(@JsonProperty("dlp") DLP dlp) {
        this.dlp = dlp;
      }
    }

    List<ItemContainer> cgp;
    List<DLPDetailsContainer> dlps;

    @JsonCreator
    public Details(
        @JsonProperty("cgp") List<ItemContainer> cgp,
        @JsonProperty("dlps") List<DLPDetailsContainer> dlps)
    {
      this.cgp = cgp;
      this.dlps = dlps;
    }
  }

  @Value
  public static class Memberships {

    @Value
    public static class MembershipDLPContainer {

      @Value
      public static class Container {

        private MembershipsContainer memberships;

        @JsonCreator
        public Container(@JsonProperty("memberships") MembershipsContainer memberships) {
          this.memberships = memberships;
        }
      }

      private Container dlp;

      @JsonCreator
      public MembershipDLPContainer(@JsonProperty("dlp") Container dlp) {
        this.dlp = dlp;
      }
    }

    MembershipsContainer cgp;
    List<MembershipDLPContainer> dlps;

    @JsonCreator
    public Memberships(
        @JsonProperty("cgp") MembershipsContainer cgp,
        @JsonProperty("dlps") List<MembershipDLPContainer> dlps)
    {
      this.cgp = cgp;
      this.dlps = dlps;
    }
  }

  CGP cgp;
  List<DLPContainer> dlps;
  Details details;
  Memberships memberships;

  @Getter(lazy = true)
  private final Map<String, String> cgpDetails = createCgpDetails();
  @Getter(lazy = true)
  private final List<Map<String, String>> dlpDetails = createDlpDetails();

  @JsonCreator
  public CGPResponse(
      @JsonProperty("cgp") CGP cgp,

      @JsonProperty("dlps") List<DLPContainer> dlps,

      @JsonProperty("details") Details details,

      @JsonProperty("memberships") Memberships memberships)
  {
    this.cgp = cgp;
    this.dlps = dlps;
    this.details = details;
    this.memberships = memberships;
  }

  public String getCGPId() {
    return cgp.getNid();
  }

  public String getCGPName() {
    return cgp.getName();
  }

  public String getCGPOrganSystem() {
    return cgp.getOrganSystem();
  }

  public String getCGPCountry() {
    return cgp.getCountry();
  }

  private Map<String, String> createCgpDetails() {
    val result = new ImmutableMap.Builder<String, String>();
    if (details == null) return result.build();

    for (val container : details.getCgp()) {
      if (container.getItem().getValue() != null) {
        result.put(container.getItem().getKey(), container.getItem().getValue());
      }
    }

    return result.build();
  }

  private List<Map<String, String>> createDlpDetails() {
    val result = new ImmutableList.Builder<Map<String, String>>();
    if (details == null) return result.build();

    for (val dlpContainer : details.getDlps()) {
      val map = new ImmutableMap.Builder<String, String>();
      for (val container : dlpContainer.getDlp().getDetails()) {
        if (container.getItem().getValue() != null) {
          map.put(container.getItem().getKey(), container.getItem().getValue());
        }
      }

      result.add(map.build());
    }

    return result.build();
  }

}
