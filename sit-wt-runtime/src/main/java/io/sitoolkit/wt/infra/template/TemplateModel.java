package io.sitoolkit.wt.infra.template;

import java.util.Map;

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
   * テンプレート内でこのオブジェクトを参照する変数名
   */
  private String var;
  /**
   * テンプレート内で参照するプロパティ
   */
  private Map<String, String> properties;

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

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
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
