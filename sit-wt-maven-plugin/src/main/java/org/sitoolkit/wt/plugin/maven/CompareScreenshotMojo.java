package org.sitoolkit.wt.plugin.maven;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sitoolkit.wt.app.compareevidence.ScreenshotComparator;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;

/**
 * Compares screenshot files between two evidence directories.
 *
 * @author yu.takada
 *
 */
@Mojo(name = "compare-screenshot")
public class CompareScreenshotMojo extends AbstractMojo {

    @Parameter(property = "base.browser")
    private String baseBrowser;

    @Parameter(property = "evidence.base")
    private String baseEvidence;

    @Parameter(property = "evidence.target")
    private String targetEvidence;

    /**
     * Open evidence after process finish.
     */
    @Parameter(property = "evidence.open", defaultValue = "true")
    private String evidenceOpen;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        EvidenceDir targetDir = targetEvidence == null ? EvidenceDir.getLatest()
                : EvidenceDir.getInstance(targetEvidence);

        String browser = StringUtils.defaultString(baseBrowser, targetDir.getBrowser());

        EvidenceDir baseDir = baseEvidence == null ? EvidenceDir.getBase(browser)
                : EvidenceDir.getInstance(baseEvidence);

        ScreenshotComparator comparator = new ScreenshotComparator();

        comparator.staticExecute(baseDir, targetDir);

    }

}
