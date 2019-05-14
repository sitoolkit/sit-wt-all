package io.sitoolkit.wt.domain.evidence;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.wt.domain.testclass.TestClassNameConverter;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class EvidenceNameConverter {

    public static String caseNo2evidence(String scriptName, String caseNo) {
        return caseNo2evidenceBase(scriptName, caseNo) + ".html";
    }

    public static String caseNo2evidenceBase(String scriptName, String caseNo) {

        String[] names = new String[] { TestClassNameConverter.script2Class(scriptName),
                TestClassNameConverter.caseNo2method(caseNo) };

        return StringUtils.join(names, ".");
    }

    public static String buildScreenshotFileName(String scriptName, String caseNo,
            String testStepNo, String itemName, String timing) {

        String evidenceBase = EvidenceNameConverter.caseNo2evidenceBase(scriptName, caseNo);
        return evidenceBase + "_"
                + StrUtils.sanitizeMetaCharacter(
                        StringUtils.join(new String[] { testStepNo, itemName, timing }, "_"))
                + ".png";
    }

}
