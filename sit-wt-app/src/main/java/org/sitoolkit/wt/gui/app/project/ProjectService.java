package org.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.util.concurrent.Executors;

import org.sitoolkit.wt.gui.app.test.SitWtRuntimeService;
import org.sitoolkit.wt.gui.domain.project.ProjectProcessClient;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.infra.config.PropertyManager;
import org.sitoolkit.wt.gui.infra.util.ResourceUtils;
import org.sitoolkit.wt.gui.infra.util.VersionUtils;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.util.infra.maven.MavenUtils;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class ProjectService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(ProjectService.class);

    SitWtRuntimeService runtimeService = new SitWtRuntimeService();

    ProjectProcessClient client = new ProjectProcessClient();

    /**
     * {@code projectDir}にpom.xmlを生成し、{@code projectSate}を初期状態に設定します。
     *
     * <h3>処理順</h3>
     *
     * <ol>
     * <li>copy classpath:/distribution-pom.xml ${projectDir}/pom.xml
     * <li>mvn dependency:build-classpath -f ${pomFile} (
     * {@link SitWtRuntimeService#loadClasspath(File, org.sitoolkit.wt.gui.infra.process.ProcessExitCallback)}
     * )
     * <li>mvn -f ${pomFile} -P unpack-property-resources (
     * {@link ProjectProcessClient#unpack(File, ProcessParams)})
     * <li>mvn dependency:build-classpath -f ${pomFile}
     * </ol>
     *
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
     * {@code projectDir}直下のpom.xmlにもとづきプロジェクトを初期化します。{@code projectSate}
     * を初期状態に設定します。
     *
     * <h3>処理順</h3>
     *
     * <ol>
     * <li>mvn dependency:build-classpath -f ${pomFile} (
     * {@link SitWtRuntimeService#loadClasspath(File, org.sitoolkit.wt.gui.infra.process.ProcessExitCallback)}
     * )
     * <li>mvn -f ${pomFile} -P unpack-property-resources (
     * {@link ProjectProcessClient#unpack(File, ProcessParams)})
     * <li>mvn dependency:build-classpath -f ${pomFile}
     * </ol>
     *
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

        ExecutorContainer.get().execute(() -> {
            runtimeService.loadClasspath(pomFile, exitCode -> {
                if (exitCode == 0) {
                    // TODO プロジェクトの初期化判定は"pom.xml内にSIT-WTの設定があること"としたい
                    projectState.init(pomFile);
                } else {
                    projectState.setState(State.NOT_LOADED);
                }
            });
        });
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
