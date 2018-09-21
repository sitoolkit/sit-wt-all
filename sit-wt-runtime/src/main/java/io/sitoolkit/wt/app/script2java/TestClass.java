package io.sitoolkit.wt.app.script2java;

import java.util.ArrayList;
import java.util.List;

import io.sitoolkit.wt.infra.template.TemplateModel;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class TestClass extends TemplateModel {

    private List<String> caseNos = new ArrayList<String>();
    /**
     * テストスクリプトが配置されているパス
     */
    private String scriptPath;
    /**
     * テストスクリプトが定義されたシート名
     */
    private String sheetName = "TestScript";

    /**
     * テストクラスのパッケージ名
     */
    private String pkg;

    public TestClass() {
        setTemplate("/testclass-template.vm");
        setVar("test");
        setFileExt("java");
    }

    public List<String> getCaseNos() {
        return caseNos;
    }

    public void setCaseNos(List<String> caseNos) {
        this.caseNos = caseNos;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String sanitizeFunctionName(String caseNo) {
        return StrUtils.sanitizeMetaCharacter(caseNo);
    }

    public String escape(String caseNo) {
        return StrUtils.addEscape(caseNo);
    }
}
