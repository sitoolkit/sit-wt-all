package org.sitoolkit.wt.gui.infra.process;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessParams {

    private List<StdoutListener> stdoutListeners = new ArrayList<>();

    private File directory;

    private List<String> command = new ArrayList<>();

    private Map<String, String> enviroment = new HashMap<>();

    private List<ProcessExitCallback> exitClallbacks = new ArrayList<>();

    public ProcessParams() {
    }

    public List<StdoutListener> getStdoutListeners() {
        return stdoutListeners;
    }

    public void setStdoutListeners(List<StdoutListener> stdoutListeners) {
        this.stdoutListeners = stdoutListeners;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public List<String> getCommand() {
        return command;
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }

    public Map<String, String> getEnviroment() {
        return enviroment;
    }

    public void setEnviroment(Map<String, String> enviroment) {
        this.enviroment = enviroment;
    }

    public List<ProcessExitCallback> getExitClallbacks() {
        return exitClallbacks;
    }

    public void setExitClallbacks(List<ProcessExitCallback> exitClallbacks) {
        this.exitClallbacks = exitClallbacks;
    }

}
