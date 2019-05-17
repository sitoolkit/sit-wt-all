package io.sitoolkit.wt.domain.testscript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class TestScriptCatalog {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(TestScriptCatalog.class);

  private Map<String, TestScript> catalog = Collections.synchronizedMap(new HashMap<>());

  @Resource
  TestScriptDao dao;

  public void add(String path, TestScript script) {
    catalog.put(path, script);
  }

  public TestScript get(String scriptPath, String sheetName) {
    TestScript script = catalog.get(scriptPath);
    if (script == null) {
      LOG.info("script.load2", scriptPath, sheetName);
      script = dao.load(scriptPath, sheetName, false);
      catalog.put(scriptPath, script);
    }
    return script;
  }

}
