package io.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.util.concurrent.Executors;

import io.sitoolkit.wt.gui.domain.project.ProjectProcessClient;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.gui.infra.util.ResourceUtils;
import io.sitoolkit.wt.gui.infra.util.VersionUtils;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.maven.MavenUtils;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class ProjectService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(ProjectService.class);

    ProjectProcessClient client = new ProjectProcessClient();

    /**
     * @param projectDir
     *            プロジェクトとするディレクトリ
     * @param projectState
     *            (inout) プロジェクトの状態
     * @return 生成したpom.xml {@code projectDir}に既にpom.xmlが存在する場合はnull
     */
    public File createProject(File projectDir, ProjectState projectState) {
        File pomFile = new File(projectDir, "pom.xml");

        if (pomFile.exists()) {
            return null;
        }

        createPom(pomFile, projectState);
        unpackResources(pomFile, projectDir);

        return pomFile;
    }

    /**
     * @param projectDir
     *            プロジェクトとするディレクトリ
     * @param projectState
     *            (inout) プロジェクトの状態
     * @return プロジェクトのpom.xml {@code projectDir}に既にpom.xmlが存在する場合はnull
     */
    public File openProject(File projectDir, ProjectState projectState) {
        LOG.info("app.openProject", projectDir.getAbsolutePath());
        File pomFile = new File(projectDir.getAbsolutePath(), "pom.xml");

        if (pomFile.exists()) {

            MavenUtils.setSitWtVersion(pomFile, VersionUtils.get());

            loadProject(pomFile, projectState);
            return pomFile;

        } else {
            return null;
        }
    }

    private void createPom(File pomFile, ProjectState projectState) {

        ResourceUtils.copy("distribution-pom.xml", pomFile);

        if (pomFile.exists()) {

            loadProject(pomFile, projectState);

        }
    }

    private void loadProject(File pomFile, ProjectState projectState) {
        LOG.info("app.loadProject", pomFile.getAbsolutePath());

        Executors.newSingleThreadExecutor()
                .submit(() -> MavenUtils.findAndInstall(pomFile.getParentFile().toPath()));

        File baseDir = pomFile.getAbsoluteFile().getParentFile();
        PropertyManager.get().load(baseDir);
        ProcessParams.setDefaultCurrentDir(baseDir);
        projectState.init(pomFile);
    }

    private void unpackResources(File pomFile, File projectDir) {

        ResourceUtils.copy("site.xml", new File(projectDir, "src/site/site.xml"));

        ProcessParams params = new ProcessParams();
        params.setDirectory(projectDir);

        params.getExitClallbacks().add(exitCode -> {
            File f = new File(projectDir, "src/main/resources/sit-wt-default.properties");
            f.renameTo(new File(f.getParentFile(), "sit-wt.properties"));
        });

        ExecutorContainer.get().execute(() -> {
            client.unpack(pomFile, params);
        });

    }
}
