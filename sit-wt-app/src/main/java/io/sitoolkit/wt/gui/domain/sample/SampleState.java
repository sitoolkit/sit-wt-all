package io.sitoolkit.wt.gui.domain.sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SampleState {

    private BooleanProperty running = new SimpleBooleanProperty(false);

    public SampleState() {
    }

}
