package org.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.app.test.SitWtRuntimeService;
import org.sitoolkit.wt.gui.domain.project.ProjectProcessClient;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.config.PropertyManager;
import org.sitoolkit.wt.gui.infra.log.LogUtils;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class ProjectService {

    private static final Logger LOG = LogUtils.get(ProjectService.class);

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
        LOG.log(Level.INFO, "opening project in {0}", projectDir.getAbsolutePath());
        File pomFile = new File(projectDir.getAbsolutePath(), "pom.xml");

        if (pomFile.exists()) {

            loadProject(pomFile, projectState);
            return pomFile;

        } else {
            return null;
        }
    }

    private void createPom(File pomFile, ProjectState projectState) {

        try {
            URL pomUrl = getClass().getResource("/distribution-pom.xml");
            Files.copy(pomUrl.openStream(), pomFile.toPath());
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        if (pomFile.exists()) {

            loadProject(pomFile, projectState);

        }
    }

    private void loadProject(File pomFile, ProjectState projectState) {
        LOG.log(Level.INFO, "loading project with {0}", pomFile.getAbsolutePath());
        PropertyManager.get().load(pomFile.getAbsoluteFile().getParentFile());

        runtimeService.loadClasspath(pomFile, exitCode -> {
            if (exitCode == 0) {
                // TODO プロジェクトの初期化判定は"pom.xml内にSIT-WTの設定があること"としたい
                projectState.init(pomFile);
            } else {
                projectState.setState(State.NOT_LOADED);
            }
        });
    }

    private void unpackResources(File pomFile, File projectDir) {
        ProcessParams params = new ProcessParams();
        params.setDirectory(projectDir);

        params.getExitClallbacks().add(exitCode -> {
            File f = new File(projectDir, "src/main/resources/sit-wt-default.properties");
            f.renameTo(new File(f.getParentFile(), "sit-wt.properties"));
        });

        client.unpack(pomFile, params);

    }
}
