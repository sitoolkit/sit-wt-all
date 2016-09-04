package org.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ReportOpener {

    private static String failsafeReportName = "failsafe-report.html";

    public static void main(String[] args) {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(ReportOpener.class);
        ReportOpener opener = appCtx.getBean(ReportOpener.class);
        opener.open();
    }

    public void open() {
        File evidenceDir = EvidenceUtils.getLatestEvidenceDir();
        File failsafeReport = new File(EvidenceUtils.concatPath(evidenceDir.getPath(),
                failsafeReportName));

        try {
            Desktop.getDesktop().open(failsafeReport);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
