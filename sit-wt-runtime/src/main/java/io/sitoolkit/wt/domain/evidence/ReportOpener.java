package io.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class ReportOpener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(ReportOpener.class);

    public void open(EvidenceDir targetDir) {
        File failsafeReport = targetDir.getFailsafeReport();

        try {
            Desktop.getDesktop().open(failsafeReport);
        } catch (IOException e) {
            LOG.error("report.open.error", e);
        }

    }

}
