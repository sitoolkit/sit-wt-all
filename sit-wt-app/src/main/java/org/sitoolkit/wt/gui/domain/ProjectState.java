package org.sitoolkit.wt.gui.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ProjectState {

    private BooleanProperty running = new SimpleBooleanProperty();

    private BooleanProperty initialized = new SimpleBooleanProperty();

    public BooleanProperty getRunning() {
        return running;
    }

    public BooleanProperty getInitialized() {
        return initialized;
    }
}
