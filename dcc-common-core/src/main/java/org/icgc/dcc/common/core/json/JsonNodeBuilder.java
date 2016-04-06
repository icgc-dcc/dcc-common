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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;

/**
 * Convenience {@link JsonNode} builder.
 */
public interface JsonNodeBuilder<J extends JsonNode> {

  /**
   * Construct and return the instance.
   */
  J end();

  /**
   * Factory method for an {@link ObjectNode} builder.
   */
  public static ObjectNodeBuilder object() {
    return new ObjectNodeBuilder(JsonNodeFactory.instance);
  }

  /**
   * Factory method for an {@link ArrayNode} builder.
   */
  public static ArrayNodeBuilder array() {
    return new ArrayNodeBuilder(JsonNodeFactory.instance);
  }

  final class ObjectNodeBuilder implements JsonNodeBuilder<ObjectNode> {

    /**
     * Dependencies.
     */
    private final JsonNodeFactory factory;

    /**
     * State.
     */
    private final ObjectNode thisNode;

    public ObjectNodeBuilder(@NonNull JsonNodeFactory factory) {
      this.factory = factory;
      this.thisNode = factory.objectNode();
    }

    public ObjectNodeBuilder withNull(@NonNull String field) {
      return with(field, factory.nullNode());
    }

    public ObjectNodeBuilder with(@NonNull String field, int value) {
      return with(field, factory.numberNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, boolean value) {
      return with(field, factory.booleanNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, JsonNode node) {
      thisNode.set(field, node);
      return this;
    }

    public ObjectNodeBuilder with(@NonNull String field, String value) {
      return with(field, factory.textNode(value));
    }

    @Override
    public ObjectNode end() {
      return thisNode;
    }

  }

  final class ArrayNodeBuilder implements JsonNodeBuilder<ArrayNode> {

    /**
     * Dependencies.
     */
    private final JsonNodeFactory factory;

    /**
     * State.
     */
    private final ArrayNode thisNode;

    public ArrayNodeBuilder(@NonNull JsonNodeFactory factory) {
      this.factory = factory;
      this.thisNode = this.factory.arrayNode();
    }

    public ArrayNodeBuilder with(JsonNode node) {
      thisNode.add(node);
      return this;
    }

    @Override
    public ArrayNode end() {
      return thisNode;
    }

  }

}
