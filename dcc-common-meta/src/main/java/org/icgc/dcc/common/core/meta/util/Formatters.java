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
package org.icgc.dcc.common.core.meta.util;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import org.icgc.dcc.common.core.model.ValueType;

public class Formatters {

  public static String formatClassName(String text) {
    return LOWER_UNDERSCORE.to(UPPER_CAMEL, normalizeToken(text));
  }

  public static String formatInstanceName(String className) {
    return LOWER_UNDERSCORE.to(LOWER_CAMEL, className);
  }

  public static String formatValueType(String name) {
    return ValueType.valueOf(name).getJavaType().getSimpleName();
  }

  public static String formatEnumValue(String text) {
    return normalizeToken(text).toUpperCase();
  }

  private static String normalizeToken(String text) {
    text = text.replaceAll("[ï]", "i");
    text = text.replaceAll(">=", "_GTE_");
    text = text.replaceAll("<=", "_LTE_");
    text = text.replaceAll("^-1$", "MINUS_ONE");
    text = text.replaceAll("^1$", "ONE");
    text = text.replaceAll("^2$", "TWO");
    text = text.replaceAll("^3$", "THREE");
    text = text.replaceAll("^4$", "FOUR");
    text = text.replaceAll("^5$", "FIVE");
    text = text.replaceAll("^6$", "SIX");
    text = text.replaceAll("^7$", "SEVEN");
    text = text.replaceAll("^8$", "EIGHT");
    text = text.replaceAll("^9$", "NINE");
    text = text.replaceAll("^10$", "TEN");
    text = text.replaceAll("^11$", "ELEVEN");
    text = text.replaceAll("^12$", "TWELVE");
    text = text.replaceAll("^13$", "THIRTEEN");
    text = text.replaceAll("^14$", "FOURTEEN");
    text = text.replaceAll("^15$", "FIFTEEN");
    text = text.replaceAll("^16$", "SIXTEEN");
    text = text.replaceAll("^17$", "SEVENTEEN");
    text = text.replaceAll("^18$", "EIGHTEEN");
    text = text.replaceAll("^19$", "NINETEEN");
    text = text.replaceAll("^20$", "TWEENTY");
    text = text.replaceAll("^21$", "TWEENTY_ONE");
    text = text.replaceAll("^22$", "TWEENTY_TWO");
    text = text.replaceAll("^23$", "TWEENTY_THREE");
    text = text.replaceAll("^[-]", "MINUS_");
    text = text.replaceAll("[^\\w]", "_").replaceAll("[_]+", "_").replaceAll("_$", "");
    return text.replaceAll("^(\\d+)", "VALUE$1");
  }

}
