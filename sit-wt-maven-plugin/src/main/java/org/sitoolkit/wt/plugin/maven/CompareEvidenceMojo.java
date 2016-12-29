package org.sitoolkit.wt.plugin.maven;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;

/**
 * Compares evidence (html) between directories.
 *
 *
 * @author yu.takada
 *
 */
@Mojo(name = "compare-evidence", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class CompareEvidenceMojo extends AbstractMojo {

    @Parameter(property = "base.browser")
    private String baseBrowser;

    @Parameter(property = "evidence.base")
    private String baseEvidence;

    @Parameter(property = "evidence.target")
    private String targetEvidence;

    @Parameter(property = "compareScreenshot", defaultValue = "true")
    private boolean compareScreenshot;

    @Parameter(property = "evidence.open", defaultValue = "true")
    private String evidenceOpen;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(targetEvidence);
        String browser = StringUtils.defaultString(baseBrowser, targetDir.getBrowser());
        EvidenceDir baseDir = EvidenceDir.baseEvidenceDir(baseEvidence, browser);

        DiffEvidenceGenerator.staticExecute(baseDir, targetDir, compareScreenshot, evidenceOpen);
    }

}
