/*
 * Copyright (c) 2013 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.test.fest;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import lombok.SneakyThrows;

import org.fest.assertions.api.AbstractAssert;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeAssert extends AbstractAssert<JsonNodeAssert, JsonNode> {

  private static ObjectMapper MAPPER = new ObjectMapper().enable(INDENT_OUTPUT);

  public JsonNodeAssert(JsonNode actual) {
    super(actual, JsonNodeAssert.class);
  }

  public static JsonNodeAssert assertThat(JsonNode actual) {
    return new JsonNodeAssert(actual);
  }

  @Override
  @SneakyThrows
  public JsonNodeAssert isEqualTo(JsonNode expected) {
    String expectedJson = toString(expected);
    String actualJson = toString(actual);
    JSONAssert.assertEquals(expectedJson, actualJson, false);

    return this;
  }

  @SneakyThrows
  String toString(JsonNode jsonNode) {
    return MAPPER.writeValueAsString(jsonNode);
  }

}