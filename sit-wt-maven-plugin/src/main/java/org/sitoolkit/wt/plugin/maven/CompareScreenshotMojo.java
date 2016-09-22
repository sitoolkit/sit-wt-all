package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.ScreenshotComparator;

/**
 * Compares screenshot files between two evidence directories.
 *
 * @author yu.takada
 *
 */
@Mojo(name = "compare-screenshot", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class CompareScreenshotMojo extends AbstractMojo {

    @Parameter(property = "driver.type", defaultValue = "default", required = true)
    @Deprecated
    private String driverType;

    @Deprecated
    @Parameter(property = "evidencedir.target")
    private String targetEvidenceDir;

    /**
     * Open evidence after process finish.
     */
    @Parameter(property = "evidence.open", defaultValue = "true")
    private String evidenceOpen;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        ScreenshotComparator comparator = new ScreenshotComparator();
        comparator.compare(targetEvidenceDir, driverType);
    }

}
