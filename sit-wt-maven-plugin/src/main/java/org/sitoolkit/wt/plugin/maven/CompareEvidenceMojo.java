package org.sitoolkit.wt.plugin.maven;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator;
import org.sitoolkit.wt.domain.evidence.DiffEvidence;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

/**
 * Compares evidence (html) between directories.
 *
 *
 * @author yu.takada
 *
 */
@Mojo(name = "compare-evidence", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class CompareEvidenceMojo extends AbstractMojo {

    @Parameter(defaultValue = "firefox", property = "baseBrowser")
    private String baseBrowser;

    private static final boolean isUnmatchCompare = false;

    @Parameter(property = "baseEvidence")
    private String baseEvidence;

    @Parameter(property = "targetEvidence")
    private String targetEvidence;

    @Parameter(property = "compareScreenshot", defaultValue = "false")
    private boolean compareScreenshot;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        DiffEvidenceGenerator generator = new DiffEvidenceGenerator();
        generator.setCompareEvidence(new DiffEvidence());
        generator.setTemplateEngine(new TemplateEngineVelocityImpl());
        // generator.run(baseBrowser, isUnmatchCompare);

        EvidenceDir targetDir = targetEvidence == null ? EvidenceDir.getLatest()
                : EvidenceDir.getInstance(targetEvidence);

        String browser = StringUtils.defaultString(baseBrowser, targetDir.getBrowser());

        EvidenceDir baseDir = baseEvidence == null ? EvidenceDir.getBase(browser)
                : EvidenceDir.getInstance(baseEvidence);

        generator.generate(baseDir, targetDir, compareScreenshot);
    }

}
