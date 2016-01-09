package org.sitoolkit.wt.plugin.maven;

import java.util.Map;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "debug", defaultPhase = LifecyclePhase.TEST, threadSafe = true, requiresDependencyResolution = ResolutionScope.TEST)
public class DebugMojo extends TestMojo {

    @Override
    public Map<String, String> getSystemPropertyVariables() {
        Map<String, String> map = super.getSystemPropertyVariables();
        map.put("sitwt.debug", "true");

        return map;
    }

    @Override
    protected int getEffectiveForkCount() {
        return 0;
    }

}
