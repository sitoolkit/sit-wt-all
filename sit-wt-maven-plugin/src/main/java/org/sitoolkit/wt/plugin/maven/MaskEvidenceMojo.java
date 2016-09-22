package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.MaskScreenshotGenerator;

/**
 *
 *
 * @author yu.takada
 *
 */
@Mojo(name = "mask-evidence", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class MaskEvidenceMojo extends AbstractMojo {

    @Parameter(property = "driver.type", defaultValue = "default", required = true)
    @Deprecated
    private String driverType;

    @Deprecated
    @Parameter(property = "evidencedir.target")
    private String targetEvidenceDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MaskScreenshotGenerator mask = new MaskScreenshotGenerator();
        mask.generate(targetEvidenceDir, driverType);
    }

}
