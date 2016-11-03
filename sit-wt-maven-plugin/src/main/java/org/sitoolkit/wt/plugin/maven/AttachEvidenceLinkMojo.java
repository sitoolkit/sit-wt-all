package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.EvidenceReportEditor;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;

@Mojo(name = "attach-evidence-link", defaultPhase = LifecyclePhase.VERIFY)
public class AttachEvidenceLinkMojo extends AbstractMojo {

    @Parameter(property = "evidence.target")
    private String targetEvidence;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(targetEvidence);

        EvidenceReportEditor editor = new EvidenceReportEditor();
        editor.edit(targetDir);
    }
}
