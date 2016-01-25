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
package org.icgc.dcc.common.core.meta.generator;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.write;
import static org.icgc.dcc.common.core.meta.util.Formatters.formatClassName;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.icgc.dcc.common.core.meta.Resolver.CodeListsResolver;
import org.icgc.dcc.common.core.meta.Resolver.DictionaryResolver;
import org.icgc.dcc.common.core.meta.model.CompilationUnit;
import org.icgc.dcc.common.core.meta.util.JsonNodeAdaptor;
import org.icgc.dcc.common.core.meta.util.JsonNodeRenderer;
import org.icgc.dcc.common.core.meta.util.StringRenderer;
import org.stringtemplate.v4.STGroupFile;

import com.fasterxml.jackson.databind.JsonNode;

@RequiredArgsConstructor
public class SubmissionModelGenerator {

  /**
   * Constants.
   */
  private static String TEMPLATE_FILE = "templates/submission.stg";

  /**
   * Dependencies.
   */
  @NonNull
  private final DictionaryResolver dictionaryResolver;
  @NonNull
  private final CodeListsResolver codeListsResolver;

  /**
   * Configuration.
   */
  @NonNull
  private final File outputDir;

  public void generate() throws IOException {
    val group = createGroup();

    val dictionary = dictionaryResolver.get();
    for (val schema : dictionary.get("files")) {
      val source = renderSource(group, "fileType", "schema", schema);
      System.out.println(source);

      val className = formatClassName(schema.get("name").asText());
      val unit = new CompilationUnit(getModelPackage(), className, source);
      writeSourceFile(unit);
    }

    val codeLists = codeListsResolver.get();
    for (val codeList : codeLists) {
      val source = renderSource(group, "codeList", "codeList", codeList);
      System.out.println(source);

      val className = formatClassName(codeList.get("name").asText());
      val unit = new CompilationUnit(getModelPackage(), className, source);
      writeSourceFile(unit);
    }
  }

  private String renderSource(STGroupFile group, String templateName, String modelName, JsonNode model) {
    int lineWidth = 100;
    val imports = getModelPackage() + ".*";

    // Create the source
    val template = group.getInstanceOf(templateName);
    template.addAggr("source.{year, packageName, imports}", getYear(), getModelPackage(), imports);
    template.add(modelName, model);

    return template.render(lineWidth);
  }

  private void writeSourceFile(CompilationUnit unit) throws IOException {
    val path = unit.getPackageName().replaceAll("[.]", File.separator);
    val fileName = unit.getName() + ".java";
    val classDir = new File(outputDir, path);
    val sourceFile = new File(classDir, fileName);

    classDir.mkdirs();

    write(unit.getSource(), sourceFile, UTF_8);
  }

  private static STGroupFile createGroup() {
    val group = new STGroupFile(TEMPLATE_FILE);
    group.registerRenderer(String.class, new StringRenderer());
    group.registerRenderer(JsonNode.class, new JsonNodeRenderer());
    group.registerModelAdaptor(JsonNode.class, new JsonNodeAdaptor());

    return group;
  }

  private static int getYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  private static String getModelPackage() {
    return CompilationUnit.class.getPackage().getName();
  }

}
