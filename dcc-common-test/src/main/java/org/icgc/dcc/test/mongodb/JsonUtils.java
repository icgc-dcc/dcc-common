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
package org.icgc.dcc.test.mongodb;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.Files.copy;
import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.core.util.MapUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.externalsorting.ExternalSort;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * General test utilities for working with JSON objects.; TODO: rename to MongoUtils
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class JsonUtils {

  public static final String MONGO_ID_FIELD = "_id";

  private static final String REPLACEMENT_VALUE = "[...]";

  /**
   * Asserts semantic JSON equality between {@code expectedFile} and {@code actualFile} using a memory efficient
   * stream-based comparison of deserialized sequences of JSON objects, ignoring transient fields.
   */
  @SneakyThrows
  public static void assertJsonFileEquals(File expectedFile, File actualFile) {
    ObjectMapper mapper = new ObjectMapper();
    MappingIterator<JsonNode> expected = mapper.reader(JsonNode.class).readValues(expectedFile);
    MappingIterator<JsonNode> actual = mapper.reader(JsonNode.class).readValues(actualFile);

    while (actual.hasNext() && expected.hasNext()) {

      // ObjectJSON.toString() seems to be the way to get the json representation, but documentation is lacking...
      @SuppressWarnings("unchecked")
      TreeMap<String, Object> expectedMap = asTreeMap(mapper.readValue(expected.nextValue().toString(), Map.class));
      @SuppressWarnings("unchecked")
      TreeMap<String, Object> actualMap = asTreeMap(mapper.readValue(actual.nextValue().toString(), Map.class));

      assertEquals("JSON mismatch between expected JSON file:\n\t" + expectedFile + "\nand actual JSON file:\n\t"
          + actualFile + "\n", expectedMap.toString(), actualMap.toString());
    }

    // Ensure same number of elements
    assertEquals(
        format("Actual JSON file is missing objects: %s, %s", expectedFile, actualFile),
        expected.hasNext(), false);
    assertEquals(
        format("Actual JSON file has additional objects: %s, %s", expectedFile, actualFile),
        actual.hasNext(), false);
  }

  /**
   * Removes transient JSON properties that can change across runs (e.g. $oid).
   */
  public static void normalizeJsonNode(JsonNode jsonNode) {
    filterTree(jsonNode, null, ImmutableList.of("$oid"), Integer.MAX_VALUE);
  }

  /**
   * Filters the supplied {@code tree} subject to {@code includedProperties}, {@code excludedProperties} and
   * {@code maxDepth}. Inclusion takes priority over exclusion.
   * 
   * @param tree the JSON object to filter
   * @param includedProperties the properties to include
   * @param excludedProperties the properties to exclude
   * @param maxDepth maximum tree depth to filter
   */
  static void filterTree(JsonNode tree, List<String> includedProperties, List<String> excludedProperties, int maxDepth) {
    filterTreeRecursive(tree, includedProperties, excludedProperties, maxDepth, null);
  }

  private static void filterTreeRecursive(JsonNode tree, List<String> includedProperties,
      List<String> excludedProperties, int maxDepth, String key) {
    Iterator<Entry<String, JsonNode>> fieldsIter = tree.fields();
    while (fieldsIter.hasNext()) {
      Entry<String, JsonNode> field = fieldsIter.next();
      String fullName = key == null ? field.getKey() : key + "." + field.getKey();

      boolean depthOk = field.getValue().isContainerNode() && maxDepth >= 0;
      boolean isIncluded = includedProperties != null && !includedProperties.contains(fullName);
      boolean isExcluded = excludedProperties != null && excludedProperties.contains(fullName);
      if ((!depthOk && !isIncluded) || isExcluded) {
        fieldsIter.remove();
        continue;
      }

      filterTreeRecursive(field.getValue(), includedProperties, excludedProperties, maxDepth - 1, fullName);
    }
  }

  /**
   * Sorts document, reorder their fields and removes the $oid field (one json document per line).
   * <p>
   * Useful for comparisons in tests. TODO: rewrite entirely as part of DCC-709
   */
  @SuppressWarnings("unchecked")
  @SneakyThrows
  public static void normalizeDumpFile(File file) {
    log.info("Normalizing file '{}'", file);

    val tempDir = Files.createTempDir();
    File format = new File(tempDir, file.getName() + ".fmt");

    {
      @Cleanup
      Reader reader = new InputStreamReader(new FileInputStream(file), UTF_8);
      @Cleanup
      BufferedReader bufferedReader = new BufferedReader(reader);

      @Cleanup
      Writer writer = new OutputStreamWriter(new FileOutputStream(format), UTF_8);
      @Cleanup
      BufferedWriter bufferedWriter = new BufferedWriter(writer);

      String line = null;
      ObjectMapper mapper = new ObjectMapper();
      while ((line = bufferedReader.readLine()) != null) {
        processLine(mapper, bufferedWriter, mapper.readValue(line, Map.class));
      }
    }

    // Use external file based sorting as to not exhaust memory
    File sort = new File(tempDir, file.getName() + ".sort");
    ExternalSort.main(new String[] { format.getAbsolutePath(), sort.getAbsolutePath() });

    copy(sort, file);
    checkState(format.delete(), "JSON format file not deleted: %s", format);
    checkState(sort.delete(), "JSON sort file not deleted: %s", sort);
    tempDir.delete();
  }

  @SneakyThrows
  private static void processLine(ObjectMapper mapper, BufferedWriter bufferedWriter, Map<String, Object> map) {
    TreeMap<String, Object> treeMap = asTreeMap(map); // for reordering
    eraseValue(treeMap, MONGO_ID_FIELD, REPLACEMENT_VALUE, false);

    @Cleanup
    StringWriter sw = new StringWriter();
    mapper.writeValue(sw, treeMap);

    bufferedWriter.write(sw.toString());
    bufferedWriter.newLine();
  }

  private static void eraseValue(TreeMap<String, Object> treeMap, String fieldName, String replacementValue) {
    eraseValue(treeMap, fieldName, replacementValue, true);
  }

  /**
   * Recursively erases a value for comparison purposes.
   */
  @SuppressWarnings("unchecked")
  private static void eraseValue(TreeMap<String, Object> treeMap, String fieldName, String replacementValue,
      boolean recursive) {

    // Replace value
    if (treeMap.containsKey(fieldName)) {
      treeMap.put(fieldName, replacementValue);
    }

    // Continue recursively
    if (recursive) { // TODO: fix recursive erasure to account for _available_data_type
      for (val v : treeMap.entrySet()) {
        Object value = v.getValue();
        if (value instanceof Map) {
          eraseValue((TreeMap<String, Object>) value, fieldName, replacementValue);
        } else if (value instanceof Collection) {
          val collection = (Collection<Object>) value;
          for (Object item : collection) {
            eraseValue((TreeMap<String, Object>) item, fieldName, replacementValue);
          }
        }
      }
    }

  }

  public static TreeMap<String, Object> asTreeMap(Map<String, Object> map) {
    return MapUtils.asTreeMap(map);
  }
}
