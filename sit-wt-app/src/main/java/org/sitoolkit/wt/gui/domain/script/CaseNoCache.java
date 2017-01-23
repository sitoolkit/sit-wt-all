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
        TestScript cachedTestScript = cache.get(testScript);

        if (cachedTestScript == null) {
            return null;
        }

        if (key == toCacheKey(cachedTestScript.getFile())) {
            return cachedTestScript.getCaseNos();
        }
        return null;
    }

    private String toCacheKey(File file) {
        return file.getAbsolutePath() + ";" + file.lastModified();
    }

    class TestScript {

        private File file;

        private List<String> caseNos;

        public TestScript() {
        }

        public TestScript(File file, List<String> caseNos) {
            super();
            this.file = file;
            this.caseNos = caseNos;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public List<String> getCaseNos() {
            return caseNos;
        }

        public void setCaseNos(List<String> caseNos) {
            this.caseNos = caseNos;
        }

    }
}
