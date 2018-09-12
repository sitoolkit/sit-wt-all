package io.sitoolkit.wt.infra.template;

public class TemplateModel {

    /**
     * 出力するファイル名の拡張子
     */
    private String fileExt;

    /**
     * 出力するファイルの既定名
     */
    private String fileBase;

    /**
     * 出力ディレクトリのパス
     */
    private String outDir;
    /**
     * テンプレートのパス
     */
    private String template;
    /**
     * テンプレート内で参照する変数名
     */
    private String var;

    public String getOutDir() {
        return outDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileBase() {
        return fileBase;
    }

    public void setFileBase(String fileBase) {
        this.fileBase = fileBase;
    }

}
