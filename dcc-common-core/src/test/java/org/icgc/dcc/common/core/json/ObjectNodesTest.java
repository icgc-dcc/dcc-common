package org.icgc.dcc.common.core.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icgc.dcc.common.core.json.Jackson.$;
import static org.icgc.dcc.common.core.json.Jackson.asObjectNode;
import static org.icgc.dcc.common.core.json.ObjectNodes.mergeObjects;

import org.junit.Test;

import lombok.val;

public class ObjectNodesTest {

  @Test
  public void testMergeObjects() throws Exception {
    val source = $("{nested:{age:20}}");
    val target = $("{id:'1', nested:{weight: 30} }");
    val result = mergeObjects(asObjectNode(target), asObjectNode(source));

    assertThat(result).hasSize(2);
    assertThat(result.get("id").textValue()).isEqualTo("1");

    val nested = result.get("nested");
    assertThat(nested.get("age").asInt()).isEqualTo(20);
    assertThat(nested.get("weight").asInt()).isEqualTo(30);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeObjects_duplicate() throws Exception {
    val source = $("{id:1}");
    val target = $("{id:'1'}");
    mergeObjects(asObjectNode(target), asObjectNode(source));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeObjects_duplicateArray() throws Exception {
    val source = $("{id:[1]}");
    val target = $("{id:[1]}");
    mergeObjects(asObjectNode(target), asObjectNode(source));
  }

}
