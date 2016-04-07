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
package org.icgc.dcc.common.core.security;

import static lombok.AccessLevel.PRIVATE;

import java.io.Closeable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * This is convenience class to disable <em>all</em> SSL certificate checking / hostname verification within the JVM for
 * {@code HttpsURLConnection}s. This is typically needed for self-signed certificates which is common for controlled or
 * testing environments.
 * <p>
 * This should <em>only</em> be used in production code as a last resort since it is a very blunt instrument.
 */
@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public final class SSLCertificateValidation {

  /**
   * Constants.
   */
  private static final HostnameVerifier DEFAULT_HOSTNAME_VERIFIER = HttpsURLConnection.getDefaultHostnameVerifier();
  private static final SSLSocketFactory DEFAULT_SSL_SOCKET_FACTORY = HttpsURLConnection.getDefaultSSLSocketFactory();

  /**
   * Globally allow self-signed SSL certificates.
   * <p>
   * Returns a {@link Closeable} to be used with {@code try-with-resources} or Lombok's {@link @Cleanup}
   */
  public static AutoCloseable disable() {
    log.warn("*** DISABLING DEFAULT SSL CERTIFICATE CHECKING ***");
    val socketFactory = createSSLSocketFactory(new DumbX509TrustManager());
    setHttpsDefaults(socketFactory, new DumbHostnameVerifier());

    return SSLCertificateValidation::enable;
  }

  /**
   * Globally disable self-signed SSL certificates.
   */
  public static void enable() {
    log.info("Default SSL certificate checking enabled");
    setHttpsDefaults(DEFAULT_SSL_SOCKET_FACTORY, DEFAULT_HOSTNAME_VERIFIER); // Restore JVM default
  }

  private static void setHttpsDefaults(SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
    HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
  }

  @SneakyThrows
  private static SSLSocketFactory createSSLSocketFactory(TrustManager... trustManagers) {
    // Creates a new instance
    val sslContext = SSLContext.getInstance("TLS");

    // A null argument means use the default implementation
    sslContext.init(null, trustManagers, null);

    return sslContext.getSocketFactory();
  }

}
