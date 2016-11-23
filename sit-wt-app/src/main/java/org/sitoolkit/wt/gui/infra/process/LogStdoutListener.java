package org.sitoolkit.wt.gui.infra.process;

import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.util.LogUtils;

public class LogStdoutListener implements StdoutListener {

    private static final Logger LOG = LogUtils.get(LogStdoutListener.class);

    public LogStdoutListener() {
    }

    @Override
    public void nextLine(String line) {
        LOG.info(line);
    }

}
