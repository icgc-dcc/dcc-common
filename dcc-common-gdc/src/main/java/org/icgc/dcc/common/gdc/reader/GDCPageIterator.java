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
package org.icgc.dcc.common.gdc.reader;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.icgc.dcc.common.gdc.client.GDCClient.Pagination;
import org.icgc.dcc.common.gdc.client.GDCClient.Query;
import org.icgc.dcc.common.gdc.client.GDCClient.Result;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class GDCPageIterator implements Iterator<List<ObjectNode>> {

  /**
   * Dependencies.
   */
  private Function<Query, Result> call;

  /**
   * Configuration.
   */
  private final Query query;

  /**
   * State.
   */
  private int from;
  private Pagination pagination;

  public GDCPageIterator(Query query, Function<Query, Result> call) {
    this.call = call;
    this.query = query;
    this.from = query.getFrom();
  }

  @Override
  public boolean hasNext() {
    return hasNotStarted() || hasMorePages();
  }

  private boolean hasNotStarted() {
    return pagination == null;
  }

  private boolean hasMorePages() {
    return pagination.getPage() < pagination.getPages();
  }

  @Override
  public List<ObjectNode> next() {
    val page = call.apply(query.toBuilder().from(from).build());

    // Advance
    pagination = page.getPagination();
    from += query.getSize();

    log.info("{}", pagination);
    return page.getHits();
  }

}