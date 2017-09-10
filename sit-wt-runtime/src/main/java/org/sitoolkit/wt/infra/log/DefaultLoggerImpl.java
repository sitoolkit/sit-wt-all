package org.sitoolkit.wt.infra.log;

import org.sitoolkit.wt.infra.resource.MessageManager;
import org.slf4j.Logger;

public class DefaultLoggerImpl implements SitLogger {

    private Logger logger;

    public DefaultLoggerImpl(Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public void info(String key, Object... arguments) {
        logger.info(MessageManager.getMessage(key), arguments);
    }

    @Override
    public void debug(String key, Object... arguments) {
        logger.debug(MessageManager.getMessage(key), arguments);
    }

}
