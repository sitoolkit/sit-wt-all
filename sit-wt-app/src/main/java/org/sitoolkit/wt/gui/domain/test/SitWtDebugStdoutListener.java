package org.sitoolkit.wt.gui.domain.test;

import org.sitoolkit.wt.util.infra.process.StdoutListener;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SitWtDebugStdoutListener implements StdoutListener {

    private BooleanProperty pausing = new SimpleBooleanProperty(false);

    @Override
    public void nextLine(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }
        if (line.startsWith("テストスクリプトの実行を一時停止します。")) {
            pausing.set(true);
        } else if (line.startsWith("テスト実行を再開します。")) {
            pausing.set(false);
        }
    }

    public boolean isPausing() {
        return pausing.get();
    }

    public BooleanProperty getPausingProperty() {
        return pausing;
    }
}
