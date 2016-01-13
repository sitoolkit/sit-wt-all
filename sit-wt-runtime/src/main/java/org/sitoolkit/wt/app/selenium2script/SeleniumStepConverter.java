package org.sitoolkit.wt.app.selenium2script;

import java.util.List;

import org.sitoolkit.wt.domain.testscript.TestStep;

public interface SeleniumStepConverter {

    /**
     * SeleniumTestStepをSIToolkitのTestStepに変換します。
     *
     * @param seleniumTestScript
     *            SeleniumTestScript
     * @param caseNo
     *            ケース番号
     * @return SIToolkitのTestStep
     */
    List<TestStep> convertTestScript(SeleniumTestScript seleniumTestScript, String caseNo);

}
