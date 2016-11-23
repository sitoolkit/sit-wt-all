package org.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.app.test.SitWtRuntimeService;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.config.PropertyManager;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.LogConsole;
import org.sitoolkit.wt.gui.infra.util.LogUtils;

public class ProjectService {

    private static final Logger LOG = LogUtils.get(ProjectService.class);

    SitWtRuntimeService runtimeService = new SitWtRuntimeService();

    public ProjectService() {
    }

    public File createProject(File projectDir, ProjectState projectState) {
        File pomFile = new File(projectDir, "pom.xml");

        if (pomFile.exists()) {
            return null;
        }

        createPom(pomFile, projectState);
        unpackResources(projectDir);

        return pomFile;
    }

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

    private void unpackResources(File projectDir) {
        ConversationProcess process = new ConversationProcess();
        process.start(new LogConsole(), projectDir, SitWtRuntimeUtils.buildUnpackCommand());

        process.onExit(exitCode -> {
            File f = new File(projectDir, "src/main/resources/sit-wt-default.properties");
            f.renameTo(new File(f.getParentFile(), "sit-wt.properties"));
        });

    }
}
