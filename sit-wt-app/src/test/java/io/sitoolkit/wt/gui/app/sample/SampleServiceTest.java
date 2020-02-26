package io.sitoolkit.wt.gui.app.sample;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.gui.app.project.ProjectService;
import io.sitoolkit.wt.gui.app.project.ProjectTestUtils;
import io.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;
import io.sitoolkit.wt.gui.infra.config.ApplicationConfig;
import io.sitoolkit.wt.gui.testutil.ThreadUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class SampleServiceTest {

  ProjectService projectService = new ProjectService();

  @Resource SampleService sampleService;

  Path projectDir = Paths.get("target/sampletest").toAbsolutePath();

  @Test
  public void test() {
    SampleProjectTester tester = new SampleProjectTester();

    tester.runTest();

    ThreadUtils.waitFor("sample test isn't finished", 120000, () -> tester.finished());

    assertThat("sample test failed", tester.getExitCode(), is(0));
  }

  @After
  public void tearDown() {
    sampleService.stop();
  }

  public class SampleProjectTester {

    @Getter private Integer exitCode;

    public synchronized boolean finished() {
      return exitCode != null;
    }

    public void runTest() {
      Path pom = ProjectTestUtils.createProject(projectService, projectDir);
      insertParentRelativePath(pom);
      sampleService.create(projectDir);

      ProcessExitCallback exitCallback =
          (exitCode) -> {
            this.exitCode = exitCode;
          };

      String headless = (Boolean.valueOf(System.getProperty("headless"))) ? "true" : "false";
      SampleStartedCallback sampleStartedCallback =
          (success) -> {
            MavenProject.load(projectDir)
                .mvnw(
                    "verify",
                    "-P",
                    "parallel",
                    "-DbaseUrl=http://localhost:8280",
                    "-Ddriver.type=chrome",
                    "-Dheadless=" + headless)
                .stdout(line -> log.debug(line))
                .exitCallback(exitCallback)
                .execute();
          };

      sampleService.start(8280, projectDir, sampleStartedCallback);
    }
  }

  static void insertParentRelativePath(Path pom) {
    try {
      List<String> edittedLines =
          Files.readAllLines(pom)
              .stream()
              .map(
                  line -> {
                    if (StringUtils.contains(line, "</parent>")) {
                      return "<relativePath>../../../sit-wt-project/pom.xml</relativePath>" + line;
                    }
                    return line;
                  })
              .collect(Collectors.toList());

      Files.write(pom, edittedLines);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
