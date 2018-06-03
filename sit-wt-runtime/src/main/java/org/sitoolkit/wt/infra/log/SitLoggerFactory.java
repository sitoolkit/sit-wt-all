package org.sitoolkit.wt.infra.log;

import org.slf4j.LoggerFactory;

public class SitLoggerFactory {

    public static SitLogger getLogger(Class<?> clazz) {
        return new DefaultLoggerImpl(LoggerFactory.getLogger(clazz));
    }

}