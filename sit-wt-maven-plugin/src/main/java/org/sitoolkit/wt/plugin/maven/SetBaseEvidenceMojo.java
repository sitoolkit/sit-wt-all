package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.BaseEvidenceManager;
import org.sitoolkit.wt.app.compareevidence.MaskScreenshotGenerator;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;

/**
 * Copy target evidence files to base evidence directory. The base evidence is
 * to be compared another.
 *
 * <pre>
 * ${targetEvidenceDir}/** -&gt; ${baseEvidenceDir}/${browser}
 * </pre>
 *
 * @author yu.takada
 *
 */
@Mojo(name = "set-base-evidence")
public class SetBaseEvidenceMojo extends AbstractMojo {

    @Parameter(property = "driver.type", defaultValue = "default", required = true)
    @Deprecated
    private String driverType;

    @Deprecated
    @Parameter(property = "evidencedir.target")
    private String targetEvidenceDir;

    /**
     * Evidence directory to be copied to base evidence direcory. If not set,
     * target is latest evidence in ${project.build.directory}
     */
    @Parameter(property = "evidence.target")
    private String targetEvidence;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MaskScreenshotGenerator mask = new MaskScreenshotGenerator();
        mask.generate(targetEvidenceDir, driverType);

        EvidenceDir targetDir = targetEvidence == null ? EvidenceDir.getLatest()
                : EvidenceDir.getInstance(targetEvidence);

        BaseEvidenceManager baseEvidenceManager = new BaseEvidenceManager();
        baseEvidenceManager.setBaseEvidence(targetDir);
    }

}
