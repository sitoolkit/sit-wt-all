package io.sitoolkit.wt.util.infra.process;

import java.util.ArrayList;
import java.util.List;

public class StdoutListenerContainer {

    private static StdoutListenerContainer instance = new StdoutListenerContainer();

    private List<StdoutListener> listeners = new ArrayList<>();

    private StdoutListenerContainer() {
    }

    public static StdoutListenerContainer get() {
        return instance;
    }

    public List<StdoutListener> getListeners() {
        return listeners;
    }

}
