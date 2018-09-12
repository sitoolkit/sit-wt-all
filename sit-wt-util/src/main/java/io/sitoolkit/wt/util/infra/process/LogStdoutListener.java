package io.sitoolkit.wt.util.infra.process;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogStdoutListener implements StdoutListener {

    private Logger log;

    private Level level;

    private String name;

    public LogStdoutListener(Logger log, Level level, String name) {
        super();
        this.log = log;
        this.level = level;
        this.name = name;
    }

    @Override
    public void nextLine(String line) {
        log.log(level, "[{0}] {1}", new Object[] { name, line });
    }

}
