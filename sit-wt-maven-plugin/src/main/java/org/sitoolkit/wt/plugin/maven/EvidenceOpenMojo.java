package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;

@Mojo(name = "open-evidence", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class EvidenceOpenMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        EvidenceOpener opener = new EvidenceOpener();
        opener.open();
    }

}
