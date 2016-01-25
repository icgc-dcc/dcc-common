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
package org.icgc.dcc.common.core.mail;

import static javax.mail.Message.RecipientType.TO;
import static lombok.AccessLevel.PRIVATE;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple mailer abstraction to send emails to DCC.
 */
@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public class Mailer {

  /**
   * Constants - Defaults.
   */
  public static final String DEFAULT_MAIL_HOST = "";
  public static final String DEFAULT_MAIL_PORT = "25";
  public static final String DEFAULT_MAIL_FROM = "noreply@oicr.on.ca";
  public static final String DEFAULT_MAIL_RECIPIENT = "";

  public static final Format DEFAULT_MAIL_FORMAT = Format.HTML;
  public static final boolean DEFAULT_MAIL_ENABLED = true;

  /**
   * Constants - Properties.
   */
  private static final String MAIL_SMTP_HOST = "mail.smtp.host";
  private static final String MAIL_SMTP_PORT = "mail.smtp.port";
  private static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
  private static final String MAIL_SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";

  /**
   * Configuration.
   */
  @NonNull
  private final String host;
  @NonNull
  private final String port;
  @NonNull
  private final String from;
  @NonNull
  private final String recipient;
  @NonNull
  private final Format format;
  private final boolean enabled;

  /**
   * Supported mail formats.
   */
  public enum Format {
    PLAIN,
    HTML
  }

  /**
   * Email abstraction.
   */
  public interface Email {

    String getSubject();

    String getBody();

    Format getFormat();

  }

  /**
   * Builder factor method.
   */
  public static Builder builder() {
    return new Builder();
  }

  @NonNull
  public void sendMail(Email email) {
    sendMail(email.getSubject(), email.getBody(), email.getFormat());
  }

  /**
   * Send an email to configured recipient with the supplied {@code subject} and {code body}.
   * <p>
   * Executes synchronously if the host is not {@code localhost}.
   * 
   * @param subject the mail subject
   * @param body the mail message
   * @param format the mail format
   */
  @NonNull
  public void sendMail(String subject, String body, Format format) {
    if (!enabled) {
      log.info("Mail not enabled. Skipping...");
      return;
    }

    try {
      val message = message();
      message.setFrom(address(from));
      message.addRecipient(TO, address(recipient));
      message.setSubject(subject);

      if (format == Format.HTML) {
        message.setText(body, "utf-8", "html");
      } else {
        message.setText(body);
      }

      log.info("Sending email '{}' to {}...", message.getSubject(), Arrays.toString(message.getAllRecipients()));
      Transport.send(message);
      log.info("Sent email '{}' to {}", message.getSubject(), Arrays.toString(message.getAllRecipients()));
    } catch (Exception e) {
      log.error("An error occured while emailing: ", e);
    }
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
    sendMail(subject, body, format);
  }

  private MimeMessage message() {
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

  @Data
  @Accessors(chain = true, fluent = true)
  public static class Builder {

    /**
     * Configuration.
     */
    private String host = DEFAULT_MAIL_HOST;
    private String port = DEFAULT_MAIL_PORT;
    private String from = DEFAULT_MAIL_FROM;
    private String recipient = DEFAULT_MAIL_RECIPIENT;
    private Format format = DEFAULT_MAIL_FORMAT;
    private boolean enabled = DEFAULT_MAIL_ENABLED;

    public Mailer build() {
      return new Mailer(host, port, from, recipient, format, enabled);
    }

  }

}
