package org.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ReportOpener {

    private static final Logger LOG = LoggerFactory.getLogger(ReportOpener.class);

    private static String FAILSAFE_REPORT_NAME = "failsafe-report.html";

    public static void main(String[] args) {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(ReportOpener.class);
        ReportOpener opener = appCtx.getBean(ReportOpener.class);
        opener.open();
    }

    public void open() {
        File evidenceDir = EvidenceUtils.getLatestEvidenceDir();
        File failsafeReport = new File(evidenceDir.getPath(), FAILSAFE_REPORT_NAME);

        try {
            Desktop.getDesktop().open(failsafeReport);
        } catch (IOException e) {
            LOG.error("レポートファイルのオープンに失敗しました", e);
        }

    }

}
