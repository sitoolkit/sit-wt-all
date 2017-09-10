package org.sitoolkit.wt.infra.log;

public interface SitLogger {

    public void info(String key, Object... arguments);

    public void debug(String key, Object... arguments);

}
