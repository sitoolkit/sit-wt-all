package org.sitoolkit.wt.gui.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 
 * <dl>
 * <dt>初期化前
 * <dd>pom.xmlをロードしていない状態
 * <dt>初期化済
 * <dd>pom.xmlをロードした状態
 * <dt>実行中
 * <dd>テストを実行中
 * <dt>デバッグ中
 * <dd>テストをデバッグ中
 * <dt>ブラウザ実行中
 * <dd>page2scriptでブラウザを実行中
 * </dl>
 * 
 * @author yuichi_kuwahara
 *
 */
public class ProjectState {

    private BooleanProperty running = new SimpleBooleanProperty();

    private BooleanProperty loaded = new SimpleBooleanProperty();

    private BooleanProperty browsing = new SimpleBooleanProperty();

    private BooleanProperty debugging = new SimpleBooleanProperty();

    public enum State {
        NOT_LOADED, LOADED, RUNNING, DEBUGGING, BROWSING
    }

    public void setState(State state) {
        loaded.set(state == State.LOADED);
        running.set(state == State.RUNNING);
        browsing.set(state == State.BROWSING);
        debugging.set(state == State.DEBUGGING);
    }

    public BooleanProperty isRunning() {
        return running;
    }

    public BooleanProperty isLoaded() {
        return loaded;
    }

    public BooleanProperty isBrowsing() {
        return browsing;
    }

    public BooleanProperty isDebugging() {
        return debugging;
    }
}
