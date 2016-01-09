package org.sitoolkit.wt.plugin.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.script2java.Script2Java;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 */
@Mojo(name = "script2java", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class Script2JavaMojo extends AbstractMojo {

    @Parameter(defaultValue = "${basedir}/testscript")
    private String testscriptDirectory;

    @Component
    private BuildContext buildContext;

    @Override
    public void execute() throws MojoExecutionException {
        Script2Java.staticExecute();

        buildContext.refresh(new File(testscriptDirectory));
    }
}
