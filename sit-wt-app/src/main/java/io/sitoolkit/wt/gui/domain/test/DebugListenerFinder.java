package io.sitoolkit.wt.gui.domain.test;

import java.nio.file.Path;
import java.util.Optional;
import io.sitoolkit.wt.domain.debug.DebugListener;

public interface DebugListenerFinder {

  Optional<DebugListener> find(Path path);
}
