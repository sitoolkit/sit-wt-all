package org.sitoolkit.wt.gui.domain.script;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseNoCache {

    private Map<String, TestScript> cache = new HashMap<>();

    public CaseNoCache() {
        // TODO Auto-generated constructor stub
    }

    public void putCaesNos(File testScript, List<String> caseNos) {
        cache.put(toCacheKey(testScript), new TestScript(testScript, caseNos));
    }

    public List<String> getCaseNosIfNotModified(File testScript) {
        String key = toCacheKey(testScript);
        TestScript cachedTestScript = cache.get(key);

        if (cachedTestScript == null) {
            return null;
        }

        if (key.equals(toCacheKey(cachedTestScript))) {
            return cachedTestScript.caseNos;
        }
        return null;
    }

    private String toCacheKey(File file) {
        return file.getAbsolutePath() + ";" + file.lastModified();
    }

    private String toCacheKey(TestScript script) {
        return script.path + ";" + script.lastModified;
    }

    class TestScript {

        String path;

        long lastModified;

        List<String> caseNos;

        public TestScript(File file, List<String> caseNos) {
            super();
            this.path = file.getAbsolutePath();
            this.lastModified = file.lastModified();
            this.caseNos = caseNos;
        }
    }
}
