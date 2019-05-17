package io.sitoolkit.wt.domain.operation;

import java.util.ArrayList;
import java.util.List;
import io.sitoolkit.wt.infra.template.TemplateModel;

public class HtmlTable extends TemplateModel {

  /**
   * カラム
   */
  private List<String> columns = new ArrayList<String>();

  /**
   * 値
   */
  private List<Value> values = new ArrayList<Value>();

  public HtmlTable() {
    setTemplate("/evidence/evidence-template-dbverify-verify-result.vm");
    setVar("table");
  }

  public List<String> getColumns() {
    return columns;
  }

  public void setColumns(List<String> columns) {
    this.columns = columns;
  }

  public List<Value> getValues() {
    return values;
  }

  public void setValues(List<Value> values) {
    this.values = values;
  }

}
