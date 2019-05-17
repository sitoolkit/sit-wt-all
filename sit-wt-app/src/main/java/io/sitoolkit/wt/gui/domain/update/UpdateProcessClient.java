package io.sitoolkit.wt.gui.domain.update;

import java.io.File;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;
import io.sitoolkit.wt.gui.infra.util.VersionUtils;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class UpdateProcessClient {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(UpdateProcessClient.class);

  public UpdateProcessClient() {}

  public void checkVersion(File pomFile, VersionCheckMode mode, VersionCheckedCallback callback) {

    MavenVersionsPluginStdoutListener listener = new MavenVersionsPluginStdoutListener(
        mode.getUpdateLine(), "io.sitoolkit.wt:sit-wt-all ..");

    ProcessCommand cmd = MavenProject.load(".")
        .mvnw(mode.getPluginGoal(), "-f", pomFile.getAbsolutePath()).stdout(listener);

    cmd.getExitCallbacks().add(exitCode -> {
      if (exitCode == 0) {
        if (VersionUtils.isNewer(listener.getCurrentVersion(), listener.getNewVersion())) {
          callback.onChecked(listener.getNewVersion());
        } else {
          LOG.info("app.latestVersion", listener.getCurrentVersion());
        }
      } else {
        LOG.warn("app.updateCheckFailed", FileIOUtils.file2str(pomFile));
      }
    });
    cmd.execute();
  }

  public void dependencyCopy(File destDir, String artifact) {

    MavenProject.load(".").mvnw("dependency:copy", "-Dartifact=" + artifact,
        "-DoutputDirectory=" + destDir.getAbsolutePath()).execute();
  }
}
