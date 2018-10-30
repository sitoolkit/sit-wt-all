package io.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buidtoolhelper.maven.MavenUtils;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.process.ProcessParams;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class ProjectService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(ProjectService.class);

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

            MavenProject
                .load(projectDir.getAbsolutePath())
                .mvnw("versions:update-properties")
                .execute();

            loadProject(pomFile, projectState);
            return pomFile;

        } else {
            return null;
        }
    }

    private void createPom(File pomFile, ProjectState projectState) {

        FileIOUtils.sysRes2file("distribution-pom.xml", pomFile.toPath());

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

        Path siteXml = Paths.get(projectDir.getAbsolutePath(), "src/site/site.xml");
        FileIOUtils.sysRes2file("site.xml", siteXml);

        Path sitwtProperties = Paths.get(projectDir.getAbsolutePath(), "src/main/resources/sit-wt.properties");
        FileIOUtils.sysRes2file("sit-wt-default.properties", sitwtProperties);

        Path capabilitiesProperties = Paths.get(projectDir.getAbsolutePath(),
                "src/main/resources/capabilities.properties");
        FileIOUtils.sysRes2file("capabilities.properties", capabilitiesProperties);

    }
}
