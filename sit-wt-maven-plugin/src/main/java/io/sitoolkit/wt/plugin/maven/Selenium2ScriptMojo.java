package io.sitoolkit.wt.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import io.sitoolkit.wt.app.selenium2script.Selenium2Script;

@Mojo(name = "selenium2script", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
    requiresDependencyCollection = ResolutionScope.RUNTIME)
public class Selenium2ScriptMojo extends AbstractMojo {

  @Parameter(property = "script.overwrite", defaultValue = "false")
  private boolean overwriteScript;

  @Parameter(property = "script.open", defaultValue = "true")
  private boolean openScript;

  @Parameter(property = "selenium.outputDir", defaultValue = "testscript")
  private String seleniumOutputDir;

  @Override
  public void execute() throws MojoExecutionException {
    Selenium2Script s2s = Selenium2Script.initInstance();
    s2s.setOpenScript(openScript);
    s2s.setOverwriteScript(overwriteScript);
    s2s.setOutputDir(seleniumOutputDir);
    s2s.execute();
  }
}
