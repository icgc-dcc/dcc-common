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
package org.icgc.dcc.common.core.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoggedReport extends BaseReport {

  @NonNull
  private final Logger log;

  public LoggedReport() {
    this(LoggerFactory.getLogger(LoggedReport.class));
  }

  @Override
  public void addInfo(String info, Object... args) {
    super.addInfo(info, args);
    log.info("Info: {}", String.format(info, args));
  }

  @Override
  public void addWarning(String warning, Object... args) {
    super.addWarning(warning, args);
    log.warn("Warning: {}", String.format(warning, args));
  }

  @Override
  public void addError(String error, Object... args) {
    super.addError(error, args);
    log.error("Error: {}", String.format(error, args));
  }

  @Override
  public void addException(Throwable e) {
    super.addException(e);
    log.error("Exception: {}", e);
  }

  @Override
  public void addTimer(Stopwatch timer, String name) {
    log.info("Timer: {} = {}", name, timer);
  }

  @Override
  public void addTimer(Stopwatch timer) {
    addTimer(timer, "Running time");
  }

}
