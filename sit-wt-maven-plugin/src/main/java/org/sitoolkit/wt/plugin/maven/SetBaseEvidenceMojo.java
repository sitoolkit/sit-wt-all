package org.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.sitoolkit.wt.domain.evidence.BaseEvidenceManager;
import org.sitoolkit.wt.domain.evidence.MaskEvidenceGenerator;

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
public class SetBaseEvidenceMojo extends AbstractBaseEvidenceMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MaskEvidenceGenerator mask = new MaskEvidenceGenerator();
        mask.generate(targetEvidenceDir, driverType);

        BaseEvidenceManager baseEvidenceManager = new BaseEvidenceManager();
        baseEvidenceManager.setBaseEvidence(targetEvidenceDir, driverType);
    }

}
