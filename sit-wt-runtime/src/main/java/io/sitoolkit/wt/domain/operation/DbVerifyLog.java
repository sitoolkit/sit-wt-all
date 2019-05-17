package io.sitoolkit.wt.domain.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.sitoolkit.wt.infra.template.TemplateModel;

public class DbVerifyLog extends TemplateModel {

  private List<VerifyObj> verifyColList = new ArrayList<>();

  private List<String> invalidCols;

  private List<VerifyObj> verifyErrs;

  private List<String> mismatchedCols;

  private String verifySql;

  private Map<String, String> verifyParams;

  public List<VerifyObj> getVerifyColList() {
    return verifyColList;
  }

  public void setVerifyColList(List<VerifyObj> verifyColList) {
    this.verifyColList = verifyColList;
  }

  public List<String> getInvalidCols() {
    return invalidCols;
  }

  public void setInvalidCols(List<String> invalidCols) {
    this.invalidCols = invalidCols;
  }

  public List<VerifyObj> getVerifyErrs() {
    return verifyErrs;
  }

  public void setVerifyErrs(List<VerifyObj> verifyErrs) {
    this.verifyErrs = verifyErrs;
  }

  public List<String> getMismatchedCols() {
    return mismatchedCols;
  }

  public void setMismatchedCols(List<String> mismatchedCols) {
    this.mismatchedCols = mismatchedCols;
  }

  public String getVerifySql() {
    return verifySql;
  }

  public void setVerifySql(String verifySql) {
    this.verifySql = verifySql;
  }

  public Map<String, String> getVerifyParams() {
    return verifyParams;
  }

  public void setVerifyParams(Map<String, String> verifyParams) {
    this.verifyParams = verifyParams;
  }

}
