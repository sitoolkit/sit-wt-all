package io.sitoolkit.wt.infra.log;

import org.slf4j.Logger;

import io.sitoolkit.wt.infra.resource.MessageManager;

public class DefaultLoggerImpl implements SitLogger {

    private Logger logger;

    public DefaultLoggerImpl(Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public void info(String key) {
        logger.info(MessageManager.getMessage(key));
    }

    @Override
    public void info(String key, Throwable t) {
        logger.info(MessageManager.getMessage(key), t);
    }

    @Override
    public void info(String key, Object... arguments) {
        logger.info(MessageManager.getMessage(key), arguments);
    }

    @Override
    public void infoMsg(String msg) {
        logger.info(msg);
    }

    @Override
    public void debug(String key) {
        logger.debug(MessageManager.getMessage(key));
    }

    @Override
    public void debug(String key, Throwable t) {
        logger.debug(MessageManager.getMessage(key), t);
    }

    @Override
    public void debug(String key, Object... arguments) {
        logger.debug(MessageManager.getMessage(key), arguments);
    }

    @Override
    public void debugMsg(String msg) {
        logger.debug(msg);
    }

    @Override
    public void warn(String key) {
        logger.warn(MessageManager.getMessage(key));
    }

    @Override
    public void warn(String key, Throwable t) {
        logger.warn(MessageManager.getMessage(key), t);
    }

    @Override
    public void warn(String key, Object... arguments) {
        logger.warn(MessageManager.getMessage(key), arguments);
    }

    @Override
    public void warnMsg(String msg) {
        logger.warn(msg);
    }

    @Override
    public void error(String key) {
        logger.error(MessageManager.getMessage(key));
    }

    @Override
    public void error(String key, Throwable t) {
        logger.error(MessageManager.getMessage(key), t);
    }

    @Override
    public void error(String key, Object... arguments) {
        logger.error(MessageManager.getMessage(key), arguments);
    }

    @Override
    public void errorMsg(String msg) {
        logger.error(msg);
    }

    @Override
    public void trace(String key) {
        logger.trace(MessageManager.getMessage(key));
    }

    @Override
    public void trace(String key, Throwable t) {
        logger.trace(MessageManager.getMessage(key), t);
    }

    @Override
    public void trace(String key, Object... arguments) {
        logger.trace(MessageManager.getMessage(key), arguments);
    }

}
