package org.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportOpener {

    private static final Logger LOG = LoggerFactory.getLogger(ReportOpener.class);

    public void open(EvidenceDir targetDir) {
        File failsafeReport = targetDir.getFailsafeReport();

        try {
            Desktop.getDesktop().open(failsafeReport);
        } catch (IOException e) {
            LOG.error("レポートファイルのオープンに失敗しました", e);
        }

    }

}
