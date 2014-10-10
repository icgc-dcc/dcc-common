package org.icgc.dcc.common.core.meta;

import lombok.val;

import org.icgc.dcc.common.core.meta.MetadataGenerator;
import org.icgc.dcc.common.core.util.resolver.RestfulDictionaryResolver;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MetadataGeneratorTest {

  @Test
  public void testGenerate() {
    val generator = new MetadataGenerator(new RestfulDictionaryResolver());
    System.out.println(generator.generate());
  }

}
