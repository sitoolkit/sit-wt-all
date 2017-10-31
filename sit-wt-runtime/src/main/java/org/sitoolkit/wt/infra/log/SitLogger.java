package org.sitoolkit.wt.infra.log;

public interface SitLogger {

    public void info(String key, Object... arguments);

    public void debug(String key, Object... arguments);

    public void warn(String key, Object... arguments);

    public void error(String key, Object... arguments);

    public void trace(String key, Object... arguments);

}
