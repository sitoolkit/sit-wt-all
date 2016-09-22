package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.sitoolkit.wt.app.compareevidence.EvidenceReportEditor;

@Mojo(name = "attach-evidence-link", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class AttachEvidenceLinkMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        EvidenceReportEditor editor = new EvidenceReportEditor();
        editor.attachEvidenceLink();
    }
}
