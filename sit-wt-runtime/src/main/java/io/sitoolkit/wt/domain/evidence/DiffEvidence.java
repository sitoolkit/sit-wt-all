package io.sitoolkit.wt.domain.evidence;

import io.sitoolkit.wt.infra.template.TemplateModel;

public class DiffEvidence extends TemplateModel {

    private String evidenceName;

    private String leftFileName;

    private String leftFile;

    private String rightFileName;

    private String rightFile;

    public DiffEvidence() {
        setTemplate("/evidence/evidence-template-diff.vm");
        setVar("diff");
        setFileExt("html");
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
    }

    public String getLeftFileName() {
        return leftFileName;
    }

    public void setLeftFileName(String leftFileName) {
        this.leftFileName = leftFileName;
    }

    public String getLeftFile() {
        return leftFile;
    }

    public void setLeftFile(String leftFile) {
        this.leftFile = leftFile;
    }

    public String getRightFileName() {
        return rightFileName;
    }

    public void setRightFileName(String rightFileName) {
        this.rightFileName = rightFileName;
    }

    public String getRightFile() {
        return rightFile;
    }

    public void setRightFile(String rightFile) {
        this.rightFile = rightFile;
    }

}
