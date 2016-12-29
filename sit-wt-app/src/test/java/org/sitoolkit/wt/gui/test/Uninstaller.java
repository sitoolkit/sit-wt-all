package org.sitoolkit.wt.gui.test;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.log.LogUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

public class Uninstaller {

    private static final Logger LOG = LogUtils.get(Uninstaller.class);

    public static void main(String[] args) {
        uninstall(new File(SystemUtils.getSitRepository(), "maven/runtime/apache-maven-3.3.9"),
                new File(SystemUtils.getSitRepository(), "maven/download"),
                new File(SystemUtils.getSitRepository(), "sit-wt-app/repository"));

    }

    static void uninstall(File... files) {
        for (File file : files) {
            if (file.exists()) {
                delete(file);
            } else {
                LOG.log(Level.WARNING, "no such file or directory {0}", file.getAbsolutePath());
            }
        }
    }

    static void delete(File file) {
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                delete(sub);
            }
        }
        LOG.log(Level.INFO, "deleting {0}", file.getAbsolutePath());
        file.delete();
    }
}
