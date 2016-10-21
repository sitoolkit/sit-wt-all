package org.sitoolkit.wt.gui.infra;

import java.util.logging.Logger;

public class LogConsole implements Console {

    private static final Logger LOG = Logger.getLogger(LogConsole.class.getName());
    
    private ConsoleListener listener;

    public LogConsole(ConsoleListener listener) {
        super();
        this.listener = listener;
    }

    public LogConsole() {
    }

    @Override
    public void append(String str) {
        if (listener != null) {
            listener.readLine(str);
        }
        LOG.info(str);
    }

}
