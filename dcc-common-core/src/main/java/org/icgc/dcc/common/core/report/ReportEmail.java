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
package org.icgc.dcc.common.core.report;

import org.icgc.dcc.common.core.mail.Mailer.Email;
import org.icgc.dcc.common.core.mail.Mailer.Format;

import com.google.common.base.Throwables;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ReportEmail implements Email {

  @NonNull
  private final String name;
  @NonNull
  private final BufferedReport report;

  @Override
  public String getSubject() {
    return name + " - " + getStatus();
  }

  @Override
  public Format getFormat() {
    return Format.HTML;
  }

  @Override
  public String getBody() {
    val body = new StringBuilder();
    body.append("<html>");
    body.append("<body>");
    body.append("<h1 style='color: " + getColor() + "; border: 3px solid " + getColor()
        + "; border-left: none; border-right: none; padding: 5px 0;'>");
    body.append(getStatus());
    body.append("</h1>");
    body.append("Finished in ").append("<b>").append(getDuration()).append("</b>");
    body.append("<br>");

    if (isError()) {
      body.append(formatSectionHeading("Exceptions"));
      body.append("<ol>");
      for (val exception : report.getExceptions()) {
        body.append("<li>");
        body.append("<h3>Message</h3>");
        body.append("<pre>");
        body.append(exception.getMessage());
        body.append("</pre>");
        body.append("<h3>Stack Trace</h3>");
        body.append("<pre>");
        body.append(Throwables.getStackTraceAsString(exception));
        body.append("</pre>");
        body.append("</li>");
      }
      body.append("</ol>");
    }

    if (!report.getErrors().isEmpty()) {
      body.append(formatSectionHeading("Errors"));
      body.append("<ol>");
      for (val error : report.getErrors()) {
        body.append("<li>").append(error).append("</li>");
      }
      body.append("</ol>");
    }

    if (!report.getWarnings().isEmpty()) {
      body.append(formatSectionHeading("Warnings"));
      body.append("<ol>");
      for (val warning : report.getWarnings()) {
        body.append("<li>").append(warning).append("</li>");
      }
      body.append("</ol>");
    }

    if (!report.getInfos().isEmpty()) {
      body.append(formatSectionHeading("Info"));
      body.append("<ol>");
      for (val info : report.getInfos()) {
        body.append("<li>").append(info).append("</li>");
      }
      body.append("</ol>");
    }

    if (!report.getTimers().isEmpty()) {
      body.append(formatSectionHeading("Timers"));
      body.append("<ol>");
      for (val entry : report.getTimers().entrySet()) {
        body.append("<li>");
        body.append("<b>" + entry.getKey() + " Timer</b>: ");
        body.append(entry.getValue());
        body.append("</li>");
      }
      body.append("</ol>");
    }

    body.append("</body>");
    body.append("</html>");

    return body.toString();
  }

  private String formatSectionHeading(String title) {
    return "<h2 style='border: 1px solid " + getColor()
        + "; border-left: none; border-right: none; margin-top 7px; margin-bottom: 6px;'>" + title
        + "</h2>";
  }

  private String getDuration() {
    val timer = report.getTimers().get("main");
    return timer == null ? "unknown duration" : timer.toString();
  }

  private String getColor() {
    if (isError()) {
      return "#FF0000";
    } else if (isWarning()) {
      return "#FF9A00";
    } else {
      return "#1A9900";
    }
  }

  private String getStatus() {
    if (isError()) {
      return "ERROR";
    } else if (isWarning()) {
      return "WARNING";
    } else {
      return "SUCCESS";
    }
  }

  private boolean isError() {
    return report.getErrorCount() > 0 || report.getExceptionCount() > 0;
  }

  private boolean isWarning() {
    return report.getWarningCount() > 0;
  }

}
