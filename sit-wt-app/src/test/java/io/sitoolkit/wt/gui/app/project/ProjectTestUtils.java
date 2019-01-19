package io.sitoolkit.wt.gui.app.project;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.testutil.ThreadUtils;

public class ProjectTestUtils {

    private ProjectTestUtils() {
    }

    public static void createProject(ProjectService service, Path projectDir) {
        MavenUtils.findAndInstall();

        if (projectDir.toFile().exists()) {
            try {
                FileUtils.deleteDirectory(projectDir.toFile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        projectDir.toFile().mkdirs();

        ProjectState projectState = new ProjectState();
        File pomFile = service.createProject(projectDir.toFile(), projectState);

        ThreadUtils.waitFor("pom.xml is not exist", () -> pomFile.exists());
        ThreadUtils.waitFor("project state is not loaded", () -> projectState.isLoaded().get());
        ThreadUtils.waitFor("capabilities.properties does not exist", () -> projectDir
                .resolve("src/main/resources/capabilities.properties").toFile().exists());
        ThreadUtils.waitFor("sit-wt.properties does not exist",
                () -> projectDir.resolve("src/main/resources/sit-wt.properties").toFile().exists());
    }
}
