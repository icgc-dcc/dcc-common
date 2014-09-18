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
package org.icgc.dcc.icgc.client.api.cgp;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A client that can query CGP entity types.
 * 
 * @see <a href="https://wiki.oicr.on.ca/display/icgcweb/ICGC.org+APIs+v2.0">ICGC.org APIs v2.0</a>
 */
public interface CGPClient {

  /**
   * Returns full list of all {@link CancerGenomeProject} with identifiers.
   * 
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773210">Search - All CGP (v2.0)</a>
   */
  List<CancerGenomeProject> getCancerGenomeProjects();

  /**
   * Returns one {@link CancerGenomeProject}.
   * 
   * @throws NoSuchElementException
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773212">Search - One CGP (Basic) (v2.0)</a>
   */
  CancerGenomeProject getCancerGenomeProject(String id);

  /**
   * Returns one {@link DataLevelProject}.
   * 
   * @throws NoSuchElementException
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773212">Search - One CGP (Basic) (v2.0)</a>
   */
  DataLevelProject getDataLevelProject(String id);

  /**
   * Return CGP or DLP details in all consequent requests.
   * 
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773214">Search - One CGP (Details) (v2.0)</a>
   */
  CGPClient details();

  /**
   * Return members information in all consequent requests.
   * 
   * @see <a href="https://wiki.oicr.on.ca/pages/viewpage.action?pageId=57773216">Search - One CGP (Memberships)
   * (v2.0)</a>
   */
  CGPClient memberships();

}
