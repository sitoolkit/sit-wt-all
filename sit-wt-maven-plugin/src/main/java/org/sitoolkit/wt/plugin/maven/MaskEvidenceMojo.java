package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.sitoolkit.wt.domain.evidence.MaskEvidenceGenerator;

/**
 *
 *
 * @author yu.takada
 *
 */
@Mojo(name = "mask-evidence", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class MaskEvidenceMojo extends AbstractBaseEvidenceMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MaskEvidenceGenerator mask = new MaskEvidenceGenerator();
        mask.generate(targetEvidenceDir, driverType);
    }

}
