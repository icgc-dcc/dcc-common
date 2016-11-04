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
package org.icgc.dcc.common.ega.util;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.hash.Hashing.md5;
import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;

import org.icgc.dcc.common.core.security.SSLCertificateValidation;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * The EGA certificate does not come installed in the default Java key store. This class downloads and caches a keystore
 * of the certificate for use on the classpath to get around this.
 * 
 * @see https://www.sslshopper.com/ssl-checker.html#hostname=https://ega.ebi.ac.uk
 * @see http://stackoverflow.com/questions/10077714/adding-certificate-to-keystore-using-java-code
 * @see https://web.archive.org/web/20130319003303/http://dzone.com/snippets/ssl-download-certificate-chain
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class EGACertificates {

  /**
   * Constants.
   */
  private static final String HOST_NAME = "ega.ebi.ac.uk";
  private static final String HOST_URL = "https://" + HOST_NAME;

  private static final String KEY_STORE_DIR = "src/main/resources";
  private static final String KEY_STORE_FILE = HOST_NAME + ".keystore";
  private static final String KEY_STORE_PASSWORD = md5().hashString(KEY_STORE_FILE, UTF_8).toString();

  public static void main(String[] args) throws Exception {
    // Input
    val hostUrl = getHostURL();
    val certificate = getCertificate(hostUrl);

    // Output
    val keyStore = createKeyStore();
    keyStore.setCertificateEntry(hostUrl.getHost(), certificate);

    @Cleanup
    val output = new BufferedOutputStream(new FileOutputStream(getKeyStoreFile()));
    keyStore.store(output, getKeyStorePassword());
  }

  @SneakyThrows
  public static KeyStore getKeyStore() {
    val keyStoreUrl = getKeyStoreURL();
    log.info("Reading EGA certficate key store from: {}", keyStoreUrl.toExternalForm());

    val keystore = newKeyStore();
    keystore.load(keyStoreUrl.openStream(), EGACertificates.getKeyStorePassword());

    return keystore;
  }

  private static URL getKeyStoreURL() {
    return EGACertificates.class.getResource("/" + KEY_STORE_FILE);
  }

  private static char[] getKeyStorePassword() {
    return KEY_STORE_PASSWORD.toCharArray();
  }

  private static File getKeyStoreFile() {
    return new File(getKeyStoreDir(), KEY_STORE_FILE);
  }

  private static File getKeyStoreDir() {
    return new File(KEY_STORE_DIR);
  }

  private static URL getHostURL() throws MalformedURLException {
    return new URL(HOST_URL);
  }

  @SneakyThrows
  private static Certificate getCertificate(URL url) {
    // Needed for self signed certs
    @Cleanup
    val session = SSLCertificateValidation.disable();

    val connection = (HttpsURLConnection) url.openConnection();
    connection.connect();

    // Get the first certificate
    return connection.getServerCertificates()[0];
  }

  @SneakyThrows
  private static KeyStore createKeyStore() {
    val dir = getKeyStoreDir();
    if (!dir.exists()) {
      checkState(dir.mkdirs());
    }

    val keystoreFile = getKeyStoreFile();
    if (keystoreFile.exists()) {
      checkState(keystoreFile.delete());
    }
    checkState(keystoreFile.createNewFile());

    val keystore = newKeyStore();
    keystore.load(null, getKeyStorePassword());

    log.info("Writing keystore to: {}", keystoreFile.getCanonicalPath());
    @Cleanup
    val outputStream = new FileOutputStream(keystoreFile);
    keystore.store(outputStream, getKeyStorePassword());

    return keystore;
  }

  private static KeyStore newKeyStore() throws KeyStoreException {
    return KeyStore.getInstance(KeyStore.getDefaultType());
  }

}
