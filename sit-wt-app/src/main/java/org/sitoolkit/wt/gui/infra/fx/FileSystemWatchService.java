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

import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;

import javafx.scene.control.TreeItem;

public class FileSystemWatchService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(FileSystemWatchService.class);

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
            LOG.info("app.filePathRegistered",
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
                LOG.error("app.exceptionLocalizedMsg", e.getLocalizedMessage(), e);
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

                LOG.debug("app.watchParams", new Object[] { watchKey,
                        watchEvent.kind(), watchEvent.context(), eventSourcePath });

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

            LOG.info("app.ignorableException", e.getClass());

        } catch (Exception e) {

            LOG.error("app.exceptionOccurs", e);

        } finally {

            if (watchKey != null) {
                watchKey.reset();
            }
        }

    }

    void create(Path eventSourcePath, Path eventTargetPath, FileTreeItem eventSourceItem) {

        FileTreeItem childItem = eventSourceItem.addChild(eventTargetPath.toFile());
        LOG.info("app.createPath", new Object[] { eventTargetPath, childItem });
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

        LOG.info("app.deletePath",
                new Object[] { eventTargetPath, eventTargetItem, removingWatchKey });
    }
}
