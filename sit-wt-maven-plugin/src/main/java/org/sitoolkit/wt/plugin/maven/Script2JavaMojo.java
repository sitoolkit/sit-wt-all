package org.sitoolkit.wt.plugin.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sitoolkit.wt.app.script2java.Script2Java;

/**
 */
@Mojo(name = "script2java", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class Script2JavaMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/test")
    private File outputDirectory;

    @Component
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        String outdir = outputDirectory.getAbsolutePath();
        Script2Java.staticExecute(outdir);
        project.addTestCompileSourceRoot(outdir);
    }
}
