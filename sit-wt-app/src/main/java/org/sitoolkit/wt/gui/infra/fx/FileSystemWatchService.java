package org.sitoolkit.wt.gui.infra.fx;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.gui.infra.util.LogUtils;

import javafx.scene.control.TreeItem;

public class FileSystemWatchService {

    private static final Logger LOG = LogUtils.get(FileSystemWatchService.class);

    private WatchService watcher;

    private Map<Path, FileTreeItem> pathItemMap = new HashMap<>();

    private Map<WatchKey, Path> watchKeyPathMap = new HashMap<>();

    private boolean watching = true;

    public void register(FileTreeItem item) {

        File rootFile = item.getValue().getFile();

        Path path = item.getValue().getFile().getAbsoluteFile().toPath();
        pathItemMap.put(path, item);

        if (!rootFile.isDirectory()) {
            return;
        }

        try {
            WatchKey watchKey = path.register(watcher,
                    new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE },
                    com.sun.nio.file.SensitivityWatchEventModifier.HIGH);

            watchKeyPathMap.put(watchKey, path);
            LOG.log(Level.INFO, "registered {0} {1}",
                    new Object[] { path.toAbsolutePath(), watchKey });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (TreeItem<FileWrapper> childItem : item.getChildren()) {
            register((FileTreeItem) childItem);
        }
    }

    public void init() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ExecutorContainer.get().submit(() -> {

            while (watching) {
                watch();
            }

        });
    }

    public void destroy() {
        if (watcher != null) {
            try {
                watching = false;
                watcher.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    void watch() {
        WatchKey watchKey = null;

        try {
            watchKey = watcher.take();

            Path eventSourcePath = watchKeyPathMap.get(watchKey);
            FileTreeItem eventSourceItem = pathItemMap.get(eventSourcePath);

            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "{0} {1} {2} {3}", new Object[] { watchKey,
                            watchEvent.kind(), watchEvent.context(), eventSourcePath });
                }

                Object context = watchEvent.context();
                if (context == null || !(context instanceof Path)) {
                    continue;
                }
                Path eventTargetPath = eventSourcePath.resolve((Path) context);

                if (StandardWatchEventKinds.ENTRY_CREATE.equals(watchEvent.kind())) {

                    create(eventSourcePath, eventTargetPath, eventSourceItem);

                } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(watchEvent.kind())) {

                    delete(eventTargetPath, eventSourceItem);

                }

            }

        } catch (ClosedWatchServiceException e) {

            LOG.log(Level.INFO, "ignorable exception {0}", e.getClass());

        } catch (Exception e) {

            LOG.log(Level.SEVERE, "exception occurs while watching file system", e);

        } finally {

            if (watchKey != null) {
                watchKey.reset();
            }
        }

    }

    void create(Path eventSourcePath, Path eventTargetPath, FileTreeItem eventSourceItem) {

        FileTreeItem childItem = eventSourceItem.addChild(eventTargetPath.toFile());
        LOG.log(Level.INFO, "created {0} {1}", new Object[] { eventTargetPath, childItem });
        register(childItem);
    }

    void delete(Path eventTargetPath, FileTreeItem eventSourceItem) {

        FileTreeItem eventTargetItem = pathItemMap.get(eventTargetPath);
        eventSourceItem.getChildren().remove(eventTargetItem);
        pathItemMap.remove(eventTargetPath);

        WatchKey removingWatchKey = null;
        for (Entry<WatchKey, Path> entry : watchKeyPathMap.entrySet()) {
            if (entry.getValue().equals(eventTargetPath)) {
                removingWatchKey = entry.getKey();
            }
        }
        watchKeyPathMap.remove(removingWatchKey);

        LOG.log(Level.INFO, "deleted {0} {1} {2}",
                new Object[] { eventTargetPath, eventTargetItem, removingWatchKey });
    }
}
