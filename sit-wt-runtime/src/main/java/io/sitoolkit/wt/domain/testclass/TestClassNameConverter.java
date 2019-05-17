package io.sitoolkit.wt.domain.testclass;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class TestClassNameConverter {

  private static final String TEST_CLASS_SUFFIX = "IT";
  private static final String TEST_METHOD_PREFIX = "test";

  public static String script2Class(String scriptName) {
    String baseName = FilenameUtils.getBaseName(scriptName);
    baseName = StringUtils.capitalize(baseName);
    baseName = StrUtils.sanitizeMetaCharacter(baseName);
    return baseName + TEST_CLASS_SUFFIX;
  }

  public static String caseNo2method(String caseNo) {
    return TEST_METHOD_PREFIX + StrUtils.sanitizeMetaCharacter(caseNo);
  }

  public static String method2caseNo(String methodName) {
    return StringUtils.removeStart(methodName, TEST_METHOD_PREFIX);
  }

}
