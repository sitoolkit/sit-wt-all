package io.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.sitoolkit.wt.app.page2script.Page2Script;

@Mojo(name = "page2script")
public class Page2ScriptMojo extends AbstractMojo {

    @Parameter(property = "script.open", defaultValue = "true")
    private boolean openScript;

    @Override
    public void execute() throws MojoExecutionException {
        Page2Script.staticStart(openScript);
    }

}
