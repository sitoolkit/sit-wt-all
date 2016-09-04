package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.domain.evidence.ReportOpener;

@Mojo(name = "open-report", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class ReportOpenMojo extends AbstractMojo {

    @Parameter(property = "evidence.open", defaultValue = "true")
    private String evidenceOpen;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Boolean.parseBoolean(evidenceOpen)) {
            ReportOpener opener = new ReportOpener();
            opener.open();
        }
    }

}
