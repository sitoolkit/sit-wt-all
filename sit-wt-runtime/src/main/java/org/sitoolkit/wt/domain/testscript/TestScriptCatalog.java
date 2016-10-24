package org.sitoolkit.wt.domain.testscript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScriptCatalog {

    private static final Logger LOG = LoggerFactory.getLogger(TestScriptCatalog.class);

    private Map<String, TestScript> catalog = Collections.synchronizedMap(new HashMap<>());

    @Resource
    TestScriptDao dao;

    public void add(String path, TestScript script) {
        catalog.put(path, script);
    }

    public TestScript get(String scriptPath, String sheetName) {
        TestScript script = catalog.get(scriptPath);
        if (script == null) {
            LOG.info("テストスクリプトをロードします。{}, {}", scriptPath, sheetName);
            script = dao.load(scriptPath, sheetName, false);
            catalog.put(scriptPath, script);
        }
        return script;
    }

}
