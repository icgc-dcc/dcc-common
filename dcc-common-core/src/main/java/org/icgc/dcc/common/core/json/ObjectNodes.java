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
package org.icgc.dcc.common.core.json;

import static com.google.common.base.Preconditions.checkArgument;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.json.Jackson.asObjectNode;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

@NoArgsConstructor(access = PRIVATE)
public final class ObjectNodes {

  public static ObjectNode mergeObjects(@NonNull ObjectNode targetNode, @NonNull ObjectNode sourceNode) {
    val result = targetNode.deepCopy();
    val fieldNames = sourceNode.fieldNames();

    while (fieldNames.hasNext()) {
      val fieldName = fieldNames.next();
      val sourceValue = sourceNode.get(fieldName);

      if (sourceValue.isObject()) {
        val targetObject = result.path(fieldName);
        val targetValue = targetObject.isMissingNode() ? sourceValue : mergeObjects(asObjectNode(targetObject),
            asObjectNode(sourceValue));
        result.put(fieldName, targetValue);
        continue;
      }

      checkArgument(result.path(fieldName).isMissingNode(), "Found duplicate field name '%s' in parent object %s",
          fieldName, result);
      result.put(fieldName, sourceNode.get(fieldName));
    }

    return result;
  }

}
