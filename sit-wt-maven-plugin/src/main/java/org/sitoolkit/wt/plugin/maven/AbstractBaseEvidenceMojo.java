package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractBaseEvidenceMojo extends AbstractMojo {

    /**
     * Browser name whitch is used to generate target evidence.
     */
    @Parameter(property = "driver.type", defaultValue = "default", required = true)
    protected String driverType;

    /**
     * Evidence directory to be copied to base evidence direcory. If not set,
     * target is latest evidence in ${project.build.directory}
     */
    @Parameter(property = "evidencedir.target")
    protected String targetEvidenceDir;

}
