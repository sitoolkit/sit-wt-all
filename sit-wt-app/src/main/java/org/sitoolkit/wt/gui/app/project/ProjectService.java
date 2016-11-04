package org.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.config.PropertyManager;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.LogConsole;
import org.sitoolkit.wt.gui.infra.util.LogUtils;

public class ProjectService {

	private static final Logger LOG = LogUtils.get(ProjectService.class); 
	
    public ProjectService() {
    }

    public File createProject(File projectDir) {
        File pomFile = new File(projectDir, "pom.xml");

        if (pomFile.exists()) {
            return null;
        }

        createPom(pomFile);
        unpackResources(projectDir);

        return pomFile;
    }

    public File openProject(File projectDir) {
    	LOG.log(Level.INFO, "opening project in {0}", projectDir.getAbsolutePath());
        File pomFile = new File(projectDir.getAbsolutePath(), "pom.xml");

        if (pomFile.exists()) {
            loadProject(pomFile);
            return pomFile;

        } else {
            return null;
        }
    }

    private void createPom(File pomFile) {

        try {
            URL pomUrl = getClass().getResource("/distribution-pom.xml");
            Files.copy(pomUrl.openStream(), pomFile.toPath());
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        if (pomFile.exists()) {

            loadProject(pomFile);
        }
    }

    private void loadProject(File pomFile) {
    	LOG.log(Level.INFO, "loading project with {0}", pomFile.getAbsolutePath());
        PropertyManager.get().load(pomFile.getAbsoluteFile().getParentFile());
        SitWtRuntimeUtils.loadSitWtClasspath(pomFile);
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
