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
package org.icgc.dcc.common.client.impl.cgp;

import java.util.List;

import lombok.Getter;
import lombok.Value;
import lombok.val;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;

@Value
public class CGPResponseMini {

  @Value
  public static class CGP {

    @Value
    public static class DLP {

      String id;

      @JsonCreator
      public DLP(@JsonProperty("id") String id) {
        this.id = id;
      }
    }

    String id;
    List<DLP> dlp;

    @JsonCreator
    public CGP(
        @JsonProperty("id") String id,
        @JsonProperty("dlp") List<DLP> dlp)
    {
      this.id = id;
      this.dlp = dlp;
    }
  }

  CGP cgp;

  @Getter(lazy = true)
  private final List<String> dlpIds = createDlpIds();

  @JsonCreator
  public CGPResponseMini(@JsonProperty("cgp") CGP cgp) {
    this.cgp = cgp;
  }

  public String getCgpId() {
    return cgp.getId();
  }

  private List<String> createDlpIds() {
    val result = new ImmutableList.Builder<String>();
    for (val dlp : cgp.getDlp()) {
      result.add(dlp.getId());
    }

    return result.build();
  }

}
