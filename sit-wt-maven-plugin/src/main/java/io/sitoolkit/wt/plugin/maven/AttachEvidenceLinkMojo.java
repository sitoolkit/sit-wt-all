package io.sitoolkit.wt.plugin.maven;

import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import io.sitoolkit.wt.app.evidence.EvidenceReportEditor;
import io.sitoolkit.wt.domain.evidence.EvidenceDir;

@Mojo(name = "attach-evidence-link", defaultPhase = LifecyclePhase.VERIFY)
public class AttachEvidenceLinkMojo extends AbstractMojo {

  @Parameter(property = "evidence.target")
  private String targetEvidence;

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    System.setProperty("sitwt.projectDirectory", project.getBasedir().getAbsolutePath());
    EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(targetEvidence);

    if (!targetDir.exists()) {
      return;
    }

    EvidenceReportEditor.staticExecute(
        targetDir, Paths.get(project.getBasedir().getAbsolutePath(), "target/site").toString());
  }
}
