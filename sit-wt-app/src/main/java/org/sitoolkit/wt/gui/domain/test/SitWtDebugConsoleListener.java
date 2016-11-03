package org.sitoolkit.wt.gui.domain.test;

import org.sitoolkit.wt.gui.infra.process.ConsoleListener;

public class SitWtDebugConsoleListener implements ConsoleListener {

    private boolean pausing;

    @Override
    public void readLine(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }
        if (line.startsWith("テストスクリプトの実行を一時停止します。")) {
            pausing = true;
        } else if (line.startsWith("テスト実行を再開します。")) {
            pausing = false;
        }
    }

    public boolean isPausing() {
        return pausing;
    }
}
