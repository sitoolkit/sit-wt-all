package org.sitoolkit.wt.gui.infra.fx;

import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;

public class FxUtils {

    public static void bindVisible(Node node, ObservableBooleanValue visible) {
        node.visibleProperty().bind(visible);
        node.managedProperty().bind(visible);
    }

}
