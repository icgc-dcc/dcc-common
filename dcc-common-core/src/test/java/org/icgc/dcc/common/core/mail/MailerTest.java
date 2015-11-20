/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.common.core.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.util.List;

import javax.mail.Address;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lombok.SneakyThrows;
import lombok.val;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Transport.class)
public class MailerTest {

  /**
   * Class under test.
   */
  Mailer mailer;

  @Before
  public void setUp() {
    mockStatic(Transport.class);
    this.mailer = Mailer.builder().build();
  }

  @Test
  @SneakyThrows
  public void testSendMail() {
    val subject = "subject";
    val text = "text";

    mailer.sendMail(subject, text);

    verifyStatic(times(1));

    val messages = getMessages();
    assertThat(messages.size()).isEqualTo(1);

    val message = messages.get(0);

    assertThat(message.getFrom()).contains(address(Mailer.DEFAULT_MAIL_FROM));
    assertThat(message.getAllRecipients()).contains(address(Mailer.DEFAULT_MAIL_RECIPIENT));

    assertThat(message.getSubject()).endsWith(subject);
    assertThat(message.getContent()).isEqualTo(text);
  }

  @SneakyThrows
  private static List<MimeMessage> getMessages() {
    val captor = ArgumentCaptor.forClass(MimeMessage.class);
    Transport.send(captor.capture());

    return captor.getAllValues();
  }

  @SneakyThrows
  private static Address address(String email) {
    return new InternetAddress(email, email);
  }

}
