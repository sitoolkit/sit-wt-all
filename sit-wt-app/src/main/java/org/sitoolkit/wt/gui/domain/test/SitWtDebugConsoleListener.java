package org.sitoolkit.wt.gui.domain.test;

import org.sitoolkit.wt.gui.infra.process.ConsoleListener;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SitWtDebugConsoleListener implements ConsoleListener {

    private BooleanProperty pausing = new SimpleBooleanProperty(false);

    @Override
    public void readLine(String line) {
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
