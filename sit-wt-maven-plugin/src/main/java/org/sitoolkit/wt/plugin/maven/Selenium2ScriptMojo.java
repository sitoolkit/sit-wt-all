package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.sitoolkit.wt.app.selenium2script.Selenium2Script;

@Mojo(name = "selenium2script", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, requiresDependencyCollection = ResolutionScope.RUNTIME)
public class Selenium2ScriptMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        Selenium2Script.initInstance().execute();
    }
}
