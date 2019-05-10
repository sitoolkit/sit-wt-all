package io.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class ReportOpener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(ReportOpener.class);

    public void open(EvidenceDir targetDir) {
        Path failsafeReport = targetDir.getFailsafeReport();

        try {
            Desktop.getDesktop().open(failsafeReport.toFile());
        } catch (IOException e) {
            LOG.error("report.open.error", e);
        }

    }

}
