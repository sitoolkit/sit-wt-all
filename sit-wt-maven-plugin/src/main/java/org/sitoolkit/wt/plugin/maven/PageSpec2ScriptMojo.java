package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.sitoolkit.wt.app.pagespec2script.PageSpec2Script;

@Mojo(name = "pagespec2script")
public class PageSpec2ScriptMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        PageSpec2Script.initInstance().execute();
    }
}
