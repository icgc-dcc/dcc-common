package org.icgc.dcc.common.hadoop.fs;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import lombok.SneakyThrows;
import lombok.val;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.icgc.dcc.common.hadoop.fs.HadoopUtils;
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

    // Need to do this because git cannot check-in empty dirs and .gitkeep would not make it empty
    val dir2 = new File(new File(sourceDir, "dir1"), "dir2");
    dir2.mkdir();

    HadoopUtils.cp(fileSystem, new Path(sourceDir.getAbsolutePath()), new Path(targetDir.getAbsolutePath()));

    assertDir(sourceDir);
    assertDir(targetDir);
  }

  private static void assertDir(File root) {
    val dir1 = new File(root, "dir1");
    val file1 = new File(root, "file1.txt");
    val dotfile1 = new File(root, ".dotfile1.txt");
    val dir2 = new File(dir1, "dir2");
    val file2 = new File(dir1, "file2.txt");

    assertThat(root).exists().isDirectory();
    assertThat(dir1).exists().isDirectory();
    assertThat(file1).exists().isFile();
    assertThat(dotfile1).exists().isFile().hasContent(dotfile1.getName());
    assertThat(dir2).exists().isDirectory();
    assertThat(file2).exists().isFile();
  }

}
