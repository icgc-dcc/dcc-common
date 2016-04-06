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
import lombok.val;

/**
 * Convenience {@link JsonNode} builder.
 */
public interface JsonNodeBuilder<J extends JsonNode> {

  /**
   * Construct and return the instance.
   */
  J end();

  /**
   * Factory methods for an {@link ObjectNode} builder.
   */

  public static ObjectNodeBuilder object() {
    return object(JsonNodeFactory.instance);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, boolean v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, int v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, float v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, String v1) {
    return object().with(k1, v1);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, String v1, @NonNull String k2, String v2) {
    return object(k1, v1).with(k2, v2);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, String v1, @NonNull String k2, String v2,
      @NonNull String k3, String v3) {
    return object(k1, v1, k2, v2).with(k3, v3);
  }

  public static ObjectNodeBuilder object(@NonNull String k1, JsonNodeBuilder<?> builder) {
    return object().with(k1, builder);
  }

  public static ObjectNodeBuilder object(JsonNodeFactory factory) {
    return new ObjectNodeBuilder(factory);
  }

  /**
   * Factory methods for an {@link ArrayNode} builder.
   */

  public static ArrayNodeBuilder array() {
    return array(JsonNodeFactory.instance);
  }

  public static ArrayNodeBuilder array(@NonNull boolean... values) {
    return array().with(values);
  }

  public static ArrayNodeBuilder array(@NonNull int... values) {
    return array().with(values);
  }

  public static ArrayNodeBuilder array(@NonNull String... values) {
    return array().with(values);
  }

  public static ArrayNodeBuilder array(@NonNull JsonNodeBuilder<?>... builders) {
    return array().with(builders);
  }

  public static ArrayNodeBuilder array(JsonNodeFactory factory) {
    return new ArrayNodeBuilder(factory);
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

    private ObjectNodeBuilder(@NonNull JsonNodeFactory factory) {
      this.factory = factory;
      this.thisNode = factory.objectNode();
    }

    public ObjectNodeBuilder withNull(@NonNull String field) {
      return with(field, factory.nullNode());
    }

    public ObjectNodeBuilder with(@NonNull String field, int value) {
      return with(field, factory.numberNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, float value) {
      return with(field, factory.numberNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, boolean value) {
      return with(field, factory.booleanNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, String value) {
      return with(field, factory.textNode(value));
    }

    public ObjectNodeBuilder with(@NonNull String field, JsonNode node) {
      thisNode.set(field, node);
      return this;
    }

    public ObjectNodeBuilder with(@NonNull String field, @NonNull JsonNodeBuilder<?> builder) {
      return with(field, builder.end());
    }

    public ObjectNodeBuilder withPOJO(@NonNull String field, @NonNull Object pojo) {
      return with(field, factory.pojoNode(pojo));
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

    private ArrayNodeBuilder(@NonNull JsonNodeFactory factory) {
      this.factory = factory;
      this.thisNode = this.factory.arrayNode();
    }

    public ArrayNodeBuilder with(boolean value) {
      thisNode.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull boolean... values) {
      for (val value : values) {
        with(value);
      }
      return this;
    }

    public ArrayNodeBuilder with(int value) {
      thisNode.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull int... values) {
      for (val value : values) {
        with(value);
      }
      return this;
    }

    public ArrayNodeBuilder with(float value) {
      thisNode.add(value);
      return this;
    }

    public ArrayNodeBuilder with(String value) {
      thisNode.add(value);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull String... values) {
      for (val value : values) {
        thisNode.add(value);
      }

      return this;
    }

    public ArrayNodeBuilder with(@NonNull Iterable<String> values) {
      for (val value : values) {
        thisNode.add(value);
      }

      return this;
    }

    public ArrayNodeBuilder with(JsonNode node) {
      thisNode.add(node);
      return this;
    }

    public ArrayNodeBuilder with(@NonNull JsonNode... nodes) {
      for (val value : nodes) {
        with(value);
      }
      return this;
    }

    public ArrayNodeBuilder with(JsonNodeBuilder<?> node) {
      return with(node);
    }

    public ArrayNodeBuilder with(@NonNull JsonNodeBuilder<?>... builders) {
      for (val value : builders) {
        with(value);
      }

      return this;
    }

    @Override
    public ArrayNode end() {
      return thisNode;
    }

  }

}
