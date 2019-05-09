package io.sitoolkit.wt.domain.evidence;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.wt.domain.testclass.TestClassNameConverter;

public class EvidenceNameConverter {

    public static String caseNo2evidence(String scriptName, String caseNo) {
        return buildEvidenceFileName(TestClassNameConverter.normalizeScriptName(scriptName),
                TestClassNameConverter.normalizeCaseNo(caseNo));
    }

    public static String buildEvidenceFileName(String sanitizedScriptName, String sanitizedCaseNo) {
        return StringUtils.join(new String[] { sanitizedScriptName, sanitizedCaseNo }, "_")
                + ".html";

    }

}
