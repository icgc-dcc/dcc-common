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

import static javax.mail.Message.RecipientType.TO;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple mailer abstraction to send emails to DCC.
 */
@Slf4j
@RequiredArgsConstructor
public class Mailer {

  /**
   * Constants - Properties.
   */
  private static final String MAIL_SMTP_HOST = "mail.smtp.host";
  private static final String MAIL_SMTP_PORT = "mail.smtp.port";
  private static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
  private static final String MAIL_SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";

  /**
   * Constants - Defaults.
   */
  public static final String DEFAULT_MAIL_HOST = "smtp.oicr.on.ca";
  public static final String DEFAULT_MAIL_PORT = "25";
  public static final String DEFAULT_MAIL_FROM = "noreply@oicr.on.ca";
  public static final String DEFAULT_MAIL_RECIPIENT = "***REMOVED***";
  public static final boolean DEFAULT_MAIL_ENABLED = true;

  /**
   * Configuration.
   */
  private final String host;
  private final String port;
  private final String from;
  private final String recipient;
  private final boolean enabled;

  public Mailer() {
    this.host = DEFAULT_MAIL_HOST;
    this.port = DEFAULT_MAIL_PORT;
    this.from = DEFAULT_MAIL_FROM;
    this.recipient = DEFAULT_MAIL_RECIPIENT;
    this.enabled = DEFAULT_MAIL_ENABLED;
  }

  /**
   * Send an email to configured recipient with the supplied {@code subject} and {code body}.
   * <p>
   * Executes synchronously if the host is not {@code localhost}.
   * 
   * @param subject the mail subject
   * @param body the mail message
   */
  @NonNull
  public void sendMail(String subject, String body) {
    if (!enabled) {
      log.info("Mail not enabled. Skipping...");
      return;
    }

    try {
      val message = message();
      message.setFrom(address(from));
      message.addRecipient(TO, address(recipient));
      message.setSubject(subject);
      message.setText(body);

      log.info("Sending email '{}' to {}...", message.getSubject(), Arrays.toString(message.getAllRecipients()));
      Transport.send(message);
      log.info("Sent email '{}' to {}", message.getSubject(), Arrays.toString(message.getAllRecipients()));
    } catch (Exception e) {
      log.error("An error occured while emailing: ", e);
    }
  }

  private Message message() {
    val props = new Properties();
    props.put(MAIL_SMTP_HOST, host);
    props.put(MAIL_SMTP_PORT, port);
    props.put(MAIL_SMTP_TIMEOUT, "5000");
    props.put(MAIL_SMTP_CONNECTION_TIMEOUT, "5000");

    return new MimeMessage(Session.getDefaultInstance(props, null));
  }

  private static InternetAddress address(String email) throws UnsupportedEncodingException {
    return new InternetAddress(email, email);
  }

}
