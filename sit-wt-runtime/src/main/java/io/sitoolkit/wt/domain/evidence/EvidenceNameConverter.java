package io.sitoolkit.wt.domain.evidence;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.wt.domain.testclass.TestClassNameConverter;

public class EvidenceNameConverter {

    public static String caseNo2evidence(String scriptName, String caseNo) {
        return caseNo2evidenceBase(scriptName, caseNo) + ".html";
    }

    public static String caseNo2evidenceBase(String scriptName, String caseNo) {

        String[] names = new String[] { TestClassNameConverter.script2Class(scriptName),
                TestClassNameConverter.caseNo2method(caseNo) };

        return StringUtils.join(names, ".");
    }

}
