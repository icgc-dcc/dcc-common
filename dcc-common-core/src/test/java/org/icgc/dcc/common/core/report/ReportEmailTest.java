package org.icgc.dcc.common.core.report;

import org.icgc.dcc.common.core.mail.Mailer;
import org.icgc.dcc.common.core.mail.Mailer.Email;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import lombok.val;

@Ignore("For development only")
public class ReportEmailTest {

  @Test
  public void testReportEmail() {
    val report = new BufferedReport();
    report.addTimer(Stopwatch.createStarted());
    report.addException(new RuntimeException("This is message 1"));
    report.addException(new RuntimeException("This is message 2"));
    report.addError("Error 1");
    report.addError("Error 2");
    report.addWarning("Warning 1");
    report.addWarning("Warning 2");
    report.addInfo("Info 1");

    val message = new ReportEmail("My Report", report);

    send(message);
  }

  private void send(Email message) {
    val userName = System.getProperty("user.name");
    val email = userName + "@oicr.on.ca";

    val mailer = Mailer.builder().enabled(true).recipient(email).build();
    mailer.sendMail(message);
  }

}