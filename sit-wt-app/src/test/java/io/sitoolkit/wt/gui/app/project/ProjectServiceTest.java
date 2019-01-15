package io.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.testutil.ThreadUtils;

public class ProjectServiceTest {

    ProjectService service = new ProjectService();

    @Test
    public void testCreate() throws IOException {
        MavenUtils.findAndInstall();
        // MavenUtils.downloadRepository();

        File projectDir = new File("target/projectservicetest");
        if (projectDir.exists()) {
            FileUtils.deleteDirectory(projectDir);
        }
        projectDir.mkdirs();

        ProjectState projectState = new ProjectState();
        File pomFile = service.createProject(projectDir, projectState);

        ThreadUtils.waitFor("pom.xml is not exist", () -> pomFile.exists());
        ThreadUtils.waitFor("project state is not loaded", () -> projectState.isLoaded().get());
        ThreadUtils.waitFor("capabilities.properties does not exist",
                () -> new File(projectDir, "src/main/resources/capabilities.properties").exists());
        ThreadUtils.waitFor("sit-wt.properties does not exist",
                () -> new File(projectDir, "src/main/resources/sit-wt.properties").exists());
    }

}
