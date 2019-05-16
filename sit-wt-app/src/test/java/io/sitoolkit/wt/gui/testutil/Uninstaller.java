package io.sitoolkit.wt.gui.testutil;

import java.io.File;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.SystemUtils;

public class Uninstaller {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(Uninstaller.class);

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
        LOG.warn("app.test.noSuchFile", file.getAbsolutePath());
      }
    }
  }

  static void delete(File file) {
    if (file.isDirectory()) {
      for (File sub : file.listFiles()) {
        delete(sub);
      }
    }
    LOG.info("app.test.delete", file.getAbsolutePath());
    file.delete();
  }
}
