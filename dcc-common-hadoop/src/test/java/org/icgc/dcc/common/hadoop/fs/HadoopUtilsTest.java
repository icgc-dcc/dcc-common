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
package org.icgc.dcc.common.hadoop.fs;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.assertj.core.api.Assertions.assertThat;

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
