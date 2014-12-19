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
package org.icgc.dcc.common.core.meta;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Files.write;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.icgc.dcc.common.core.util.resolver.Resolver.DictionaryResolver;
import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import com.fasterxml.jackson.databind.JsonNode;

@RequiredArgsConstructor
public class DictionaryMetadataGenerator {

  @NonNull
  private final DictionaryResolver resolver;

  public void generate() throws IOException {
    val dictionary = resolver.get();

    val packageName = "org.icgc.dcc.common.core.meta";
    val packageDir = new File("target");

    val group = new STGroupFile("dictionary.stg");
    group.registerRenderer(JsonNode.class, new JsonNodeRenderer());
    group.registerModelAdaptor(JsonNode.class, new JsonNodeAdaptor());

    for (val schema : dictionary.get("files")) {
      val year = 2014;
      val imports = DataElement.class.getName();
      val name = schema.get("name").asText();
      val className = getClassName(name);
      val instanceName = getInstanceName(className);

      val template = group.getInstanceOf("fileType");
      template.addAggr("source.{year, packageName, imports, className, instanceName}",
          year, packageName, imports, className, instanceName);
      template.add("schema", schema);

      val source = template.render();

      writeFile(packageDir, className, source);
      System.out.println(source);
    }
  }

  private void writeFile(File packageDir, String className, String source) throws IOException {
    write(source, new File(packageDir, className + ".java"), UTF_8);
  }

  private String getClassName(String schemaName) {
    return LOWER_UNDERSCORE.to(UPPER_CAMEL, schemaName);
  }

  private String getInstanceName(String className) {
    return UPPER_CAMEL.to(LOWER_CAMEL, className);
  }

  public static class JsonNodeRenderer implements AttributeRenderer {

    @Override
    public String toString(Object o, String formatString, Locale locale) {
      val jsonNode = (JsonNode) o;

      if (jsonNode.isTextual()) {
        val text = jsonNode.asText();
        if ("className".equals(formatString)) {
          return LOWER_UNDERSCORE.to(UPPER_CAMEL, text);
        } else if ("instanceName".equals(formatString)) {
          return LOWER_UNDERSCORE.to(LOWER_CAMEL, text);
        } else if ("fieldName".equals(formatString)) {
          return LOWER_UNDERSCORE.to(LOWER_CAMEL, text);
        } else {
          return text;
        }
      } else {
        return jsonNode.toString();
      }
    }

  }

  public static class JsonNodeAdaptor implements ModelAdaptor {

    @Override
    public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName)
        throws STNoSuchPropertyException {
      val jsonNode = (JsonNode) o;

      val attribute = jsonNode.path(propertyName);
      if (attribute.isArray()) {
        return newArrayList((Iterable<?>) attribute);
      } else {
        return attribute;
      }
    }
  }

}
