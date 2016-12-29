package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.MaskEvidenceGenerator;
import org.sitoolkit.wt.app.compareevidence.MaskScreenshotGenerator;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;

/**
 *
 *
 * @author yu.takada
 *
 */
@Mojo(name = "mask-evidence")
public class MaskEvidenceMojo extends AbstractMojo {

    @Parameter(property = "evidence.target")
    private String targetEvidence;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(targetEvidence);

        MaskScreenshotGenerator mask = new MaskScreenshotGenerator();
        mask.generate(targetDir);

        MaskEvidenceGenerator evidence = new MaskEvidenceGenerator();
        evidence.generate(targetDir);

    }

}
