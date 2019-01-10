package io.sitoolkit.wt.gui.domain.project;

import java.io.File;
import java.nio.file.Path;

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

    private File pomFile;

    private File baseDir;

    private BooleanProperty running = new SimpleBooleanProperty();

    private BooleanProperty loaded = new SimpleBooleanProperty();

    private BooleanProperty browsing = new SimpleBooleanProperty();

    private BooleanProperty debugging = new SimpleBooleanProperty();

    private BooleanProperty locking = new SimpleBooleanProperty();

    public enum State {
        NOT_LOADED, LOADED, RUNNING, DEBUGGING, BROWSING, LOCKING
    }

    public void setState(State state) {
        loaded.set(state == State.LOADED);
        running.set(state == State.RUNNING);
        browsing.set(state == State.BROWSING);
        debugging.set(state == State.DEBUGGING);
        locking.set(state == State.LOCKING);
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

    public BooleanProperty isLocking() {
        return locking;
    }

    public void init(File pomFile) {
        this.pomFile = pomFile;
        baseDir = pomFile.getAbsoluteFile().getParentFile();
        setState(State.LOADED);
    }

    public void reset() {
        setState(State.LOADED);
    }

    public File getPomFile() {
        return pomFile;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public Path getBaseDirPath() {
        return getBaseDir().toPath();
    }
}
