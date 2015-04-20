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
package org.icgc.dcc.common.client.api;

import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.icgc.dcc.common.client.api.cgp.CGPClient;
import org.icgc.dcc.common.client.api.cms.CMSClient;
import org.icgc.dcc.common.client.api.cud.CUDClient;
import org.icgc.dcc.common.client.api.daco.DACOClient;
import org.icgc.dcc.common.client.api.shorturl.ShortURLClient;
import org.icgc.dcc.common.client.impl.cgp.DefaultCGPClient;
import org.icgc.dcc.common.client.impl.cms.DefaultCMSClient;
import org.icgc.dcc.common.client.impl.cud.DefaultCUDClient;
import org.icgc.dcc.common.client.impl.daco.DefaultDACOClient;
import org.icgc.dcc.common.client.impl.shorturl.DefaultShortURLClient;

/**
 * A main class to create different API clients.
 */
@AllArgsConstructor(access = PRIVATE)
public class ICGCClient {

  private final ICGCClientConfig config;

  /**
   * Get a client for CGP API requests
   * 
   * @throws IllegalArgumentException when the configuration parameters are empty or missing
   */
  public CGPClient cgp() {
    return new DefaultCGPClient(config);
  }

  /**
   * Get a client for DACO API requests
   * 
   * @throws IllegalArgumentException when the configuration parameters are empty or missing
   */
  public DACOClient daco() {
    return new DefaultDACOClient(config);
  }

  /**
   * Get a client for CUD API requests
   * 
   * @throws IllegalArgumentException when the configuration parameters are empty or missing
   */
  public CUDClient cud() {
    return new DefaultCUDClient(config);
  }

  /**
   * Get a client for ShortUrl API requests
   * 
   * @throws IllegalArgumentException when the configuration parameters are empty or missing
   */
  public ShortURLClient shortUrl() {
    return new DefaultShortURLClient(config);
  }

  /**
   * Get a client for CMS API requests
   * 
   * @throws IllegalArgumentException when the configuration parameters are empty or missing
   */
  public CMSClient cms() {
    return new DefaultCMSClient(config);
  }

  /**
   * Creates an instance of ICGCClient
   * 
   * @throws IllegalArgumentException when the configuration parameters are empty or missing
   */
  public static ICGCClient create(@NonNull ICGCClientConfig config) {
    return new ICGCClient(config);
  }

}
