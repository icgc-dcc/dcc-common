package org.icgc.dcc.common.core.meta;

import static org.assertj.core.api.Assertions.assertThat;
import lombok.val;

import org.icgc.dcc.common.core.meta.RestfulCodeListsResolver;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RestfulCodeListsResolverTest {

  @Test
  public void testResolve() {
    val resolver = new RestfulCodeListsResolver();
    val codeLists = resolver.get();
    assertThat(codeLists).hasAtLeastOneElementOfType(ObjectNode.class);
  }

}
