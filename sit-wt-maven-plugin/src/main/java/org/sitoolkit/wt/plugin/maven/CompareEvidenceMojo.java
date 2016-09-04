package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.domain.evidence.DiffEvidence;
import org.sitoolkit.wt.domain.evidence.DiffEvidenceGenerator;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

/**
 *
 * @author yu.takada
 *
 */
@Mojo(name = "compare-evidence", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class CompareEvidenceMojo extends AbstractMojo {

    @Parameter(defaultValue = "firefox", property = "main.browser")
    private String mainBrowser;

    private static final boolean isUnmatchCompare = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        DiffEvidenceGenerator generator = new DiffEvidenceGenerator();
        generator.setCompareEvidence(new DiffEvidence());
        generator.setTemplateEngine(new TemplateEngineVelocityImpl());
        generator.run(mainBrowser, isUnmatchCompare);

    }

}
