package org.sitoolkit.wt.plugin.maven;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.surefire.SurefirePlugin;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.sitoolkit.wt.app.script2java.Script2Java;
import org.sonatype.plexus.build.incremental.BuildContext;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, threadSafe = true, requiresDependencyResolution = ResolutionScope.TEST)
public class TestMojo extends SurefirePlugin {

    @Parameter(property = "baseUrl")
    private String baseUrl;

    @Parameter(property = "driverType")
    private String driverType;

    @Parameter(defaultValue = "${basedir}/testscript")
    private String testscriptDirectory;

    @Component
    private BuildContext buildContext;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Script2Java.staticExecute();

        buildContext.refresh(new File(testscriptDirectory));

        super.execute();
    }

    @Override
    public Map<String, String> getSystemPropertyVariables() {
        Map<String, String> map = super.getSystemPropertyVariables();
        if (map == null) {
            map = new HashMap<>();
        }

        if (baseUrl != null) {
            map.put("baseUrl", baseUrl);
        }
        if (driverType == null) {
            map.put("driverType", driverType);
        }

        return map;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

}
