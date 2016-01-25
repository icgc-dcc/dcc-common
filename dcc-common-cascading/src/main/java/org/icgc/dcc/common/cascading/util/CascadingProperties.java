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
package org.icgc.dcc.common.cascading.util;

import static cascading.flow.stream.StreamGraph.DOT_FILE_PATH;
import static cascading.flow.stream.StreamGraph.ERROR_DOT_FILE_NAME;
import static java.lang.String.format;
import static org.icgc.dcc.common.hadoop.util.HadoopConstants.CASCADING_DOT_FILE_PATH;
import static org.icgc.dcc.common.hadoop.util.HadoopConstants.CASCADING_ERROR_DOT_FILE_NAME;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to set common hadoop properties.
 */
@Slf4j
public class CascadingProperties {

  /**
   * DCC-1526: Enable DOT export.
   */
  public static Map<Object, Object> enableDotExports(Map<Object, Object> properties) {
    properties.put(DOT_FILE_PATH, CASCADING_DOT_FILE_PATH);
    log.info(getLogMessage(properties, DOT_FILE_PATH));

    properties.put(ERROR_DOT_FILE_NAME, CASCADING_ERROR_DOT_FILE_NAME);
    log.info(getLogMessage(properties, ERROR_DOT_FILE_NAME));

    return properties;
  }

  private static String getLogMessage(Map<Object, Object> properties, String property) {
    return format("Setting '%s' to '%s'", property, properties.get(property));
  }

}
