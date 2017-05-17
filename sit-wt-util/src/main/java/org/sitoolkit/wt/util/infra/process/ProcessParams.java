package org.sitoolkit.wt.util.infra.process;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessParams {

	private static File defaultCurrentDir;

    private List<StdoutListener> stdoutListeners = new ArrayList<>();

    private File directory;

    private List<String> command = new ArrayList<>();

    private Map<String, String> enviroment = new HashMap<>();

    private List<ProcessExitCallback> exitClallbacks = new ArrayList<>();

    private boolean processWait = false;

    public ProcessParams() {
    }

    public static File getDefaultCurrentDir() {
		return defaultCurrentDir;
	}

	public static void setDefaultCurrentDir(File defaultCurrentDir) {
		ProcessParams.defaultCurrentDir = defaultCurrentDir;
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

    public boolean isProcessWait() {
        return processWait;
    }

    public void setProcessWait(boolean processWait) {
        this.processWait = processWait;
    }

}
