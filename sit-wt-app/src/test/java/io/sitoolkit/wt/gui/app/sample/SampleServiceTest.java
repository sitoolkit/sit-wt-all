package io.sitoolkit.wt.gui.app.sample;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;

import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.gui.app.project.ProjectService;
import io.sitoolkit.wt.gui.app.project.ProjectTestUtils;
import io.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;
import io.sitoolkit.wt.gui.testutil.ThreadUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleServiceTest {

    ProjectService projectService = new ProjectService();

    SampleService sampleService = new SampleService();

    Path projectDir = Paths.get("target/sampletest").toAbsolutePath();

    @Test
    public void test() {
        SampleProjectTester tester = new SampleProjectTester();

        tester.runTest();

        ThreadUtils.waitFor("sample test isn't finished", 60000, () -> tester.finished());

        assertThat("sample test failed", tester.getExitCode(), is(0));
    }

    @After
    public void tearDown() {
        sampleService.stop(projectDir);
    }

    public class SampleProjectTester {

        @Getter
        private Integer exitCode;

        public synchronized boolean finished() {
            return exitCode != null;
        }

        public void runTest() {
            ProjectTestUtils.createProject(projectService, projectDir);
            sampleService.create(projectDir);

            ProcessExitCallback exitCallback = (exitCode) -> {
                this.exitCode = exitCode;
            };

            SampleStartedCallback sampleStartedCallback = (success) -> {
                MavenProject.load(projectDir)
                        .mvnw("verify", "-P", "parallel", "-DbaseUrl=http://localhost:8280")
                        .stdout(line -> log.debug(line)).exitCallback(exitCallback).execute();
            };

            sampleService.start(projectDir, sampleStartedCallback);

        }
    }
}
