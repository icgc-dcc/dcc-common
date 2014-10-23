/*
 * Copyright (c) 2014 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.common.core.io;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.icgc.dcc.common.core.util.Scheme.HTTP;
import static org.icgc.dcc.common.core.util.Scheme.HTTPS;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.NonNull;
import lombok.val;

public final class SchemeFileInputStream extends ForwardingInputStream {

  private final URI fileUri;

  public SchemeFileInputStream(@NonNull URI fileUri) throws IOException {
    super(readUri(fileUri));
    this.fileUri = fileUri;
  }

  @Override
  public String toString() {
    return fileUri.toString();
  }

  private static InputStream readUri(URI fileUri) throws MalformedURLException, IOException {
    val scheme = fileUri.getScheme();
    if (isWeb(scheme)) {
      val fileName = fileUri.getFragment();
      return readZipEntry(fileUri.toURL(), fileName);
    } else if (isClasspath(scheme)) {
      return readClasspath(fileUri);
    } else {
      throw new IllegalArgumentException("Unsupported scheme: '" + scheme + "'");
    }
  }

  private static InputStream readZipEntry(@NonNull URL zipUrl, String fileName) throws IOException {
    // This is required for Jenkins because it is very slow
    val connection = zipUrl.openConnection();
    connection.setReadTimeout((int) MINUTES.toMillis(30));

    // Resolve
    val zip = new ZipInputStream(connection.getInputStream());
    ZipEntry entry;

    do {
      entry = zip.getNextEntry();
    } while (!fileName.equals(entry.getName()));

    return zip;
  }

  private static InputStream readClasspath(URI fileUri) {
    return SchemeFileInputStream.class.getResourceAsStream(fileUri.getSchemeSpecificPart());
  }

  private static boolean isWeb(String scheme) {
    return HTTP.getId().equals(scheme) || HTTPS.getId().equals(scheme);
  }

  private static boolean isClasspath(String scheme) {
    return "classpath".equals(scheme);
  }

}
