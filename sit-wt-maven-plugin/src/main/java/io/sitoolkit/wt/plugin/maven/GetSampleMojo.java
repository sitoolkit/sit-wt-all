package io.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import io.sitoolkit.wt.app.sample.SampleManager;

@Mojo(name = "sample")
public class GetSampleMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        SampleManager sample = new SampleManager();
        sample.unarchiveBasicSample();
    }

}
