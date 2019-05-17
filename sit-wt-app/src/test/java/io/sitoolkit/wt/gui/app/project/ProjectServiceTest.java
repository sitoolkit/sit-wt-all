package io.sitoolkit.wt.gui.app.project;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Test;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;

public class ProjectServiceTest {

  ProjectService service = new ProjectService();

  @Test
  public void testCreate() throws IOException {

    ProjectTestUtils.createProject(service, Paths.get("target/projectservicetest"));
    MavenUtils.findAndInstall();

  }

}
