package org.icgc.dcc.hadoop.fs;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import lombok.SneakyThrows;
import lombok.val;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HadoopUtilsTest {

  private static final File TEST_DIR = new File("src/test/resources/fixtures/source");

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  FileSystem fileSystem;
  File root;

  @Before
  @SneakyThrows
  public void setUp() {
    this.fileSystem = FileSystem.getLocal(new Configuration());
    this.root = tmp.newFolder();
  }

  @Test
  public void testCopy() throws IOException {
    val sourceDir = new File(root, "source");
    val targetDir = new File(root, "target");
    copyDirectory(TEST_DIR, sourceDir);

    HadoopUtils.cp(fileSystem, new Path(sourceDir.getAbsolutePath()), new Path(targetDir.getAbsolutePath()));

    assertDir(sourceDir);
    assertDir(targetDir);
  }

  private static void assertDir(File dir) {
    val dir1 = new File(dir, "dir1");
    val file1 = new File(dir, "file1.txt");
    val dir2 = new File(dir1, "dir2");
    val file2 = new File(dir1, "file2.txt");

    assertThat(dir).exists().isDirectory();
    assertThat(dir1).exists().isDirectory();
    assertThat(file1).exists().isFile();
    assertThat(dir2).exists().isDirectory();
    assertThat(file2).exists().isFile();
  }

}
