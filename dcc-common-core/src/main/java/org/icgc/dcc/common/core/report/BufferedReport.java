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

import java.util.List;
import java.util.Map;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString(callSuper = true)
public class BufferedReport extends BaseReport {

  /**
   * Constants.
   */
  private static final int DEFAULT_MAX_ENTRIES = 100;

  /**
   * Configuration.
   */
  private final int maxEntries;

  /**
   * State.
   */
  private final List<String> infos = Lists.newArrayList();
  private final List<String> warnings = Lists.newArrayList();
  private final List<String> errors = Lists.newArrayList();
  private final List<Exception> exceptions = Lists.newArrayList();
  private final Map<String, Stopwatch> timers = Maps.newLinkedHashMap();

  public BufferedReport() {
    this(DEFAULT_MAX_ENTRIES);
  }

  @Override
  public void addInfo(String info, Object... args) {
    super.addInfo(info, args);

    if (infoCount <= maxEntries) {
      infos.add(formatMessage(info, args));
    }
  }

  @Override
  public void addWarning(String warning, Object... args) {
    super.addWarning(warning, args);

    if (warningCount <= maxEntries) {
      warnings.add(formatMessage(warning, args));
    }
  }

  @Override
  public void addError(String error, Object... args) {
    super.addError(error, args);

    if (errorCount <= maxEntries) {
      errors.add(formatMessage(error, args));
    }
  }

  @Override
  public void addException(Exception e) {
    super.addException(e);

    if (exceptionCount < maxEntries) {
      exceptions.add(e);
    }
  }

  @Override
  public void addTimer(Stopwatch timer, String name) {
    timers.put(name, timer);
  }

  @Override
  public void addTimer(Stopwatch timer) {
    addTimer(timer, "main");
  }

  private static String formatMessage(String message, Object... args) {
    if (args.length == 0) {
      return message;
    }

    try {
      return String.format(message, args);
    } catch (Exception e) {
      return message;
    }
  }

}
