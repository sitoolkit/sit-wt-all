package io.sitoolkit.wt.infra.log;

public interface SitLogger {

    public void info(String key);

    public void info(String key, Throwable t);

    public void info(String key, Object... arguments);

    public void infoMsg(String msg);

    public void debug(String key);

    public void debug(String key, Throwable t);

    public void debug(String key, Object... arguments);

    public void debugMsg(String msg);

    public void warn(String key);

    public void warn(String key, Throwable t);

    public void warn(String key, Object... arguments);

    public void warnMsg(String msg);

    public void error(String key);

    public void error(String key, Throwable t);

    public void error(String key, Object... arguments);

    public void errorMsg(String msg);

    public void trace(String key);

    public void trace(String msg, Throwable t);

    public void trace(String key, Object... arguments);

}
