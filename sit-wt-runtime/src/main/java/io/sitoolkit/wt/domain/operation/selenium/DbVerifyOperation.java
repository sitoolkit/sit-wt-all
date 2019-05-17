/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.domain.operation.selenium;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import io.sitoolkit.wt.domain.operation.DbVerifyLog;
import io.sitoolkit.wt.domain.operation.HtmlTable;
import io.sitoolkit.wt.domain.operation.Value;
import io.sitoolkit.wt.domain.operation.VerifyObj;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.VerifyException;
import io.sitoolkit.wt.infra.template.TemplateEngine;

/**
 *
 * @author yu.takada
 */
@Component("dbverifyOperation")
public class DbVerifyOperation extends SeleniumOperation {

  @Resource
  NamedParameterJdbcTemplate jdbcTemplate;

  @Resource
  HtmlTable htmlTable;

  @Resource
  DbVerifyLog dbVerifyLog;

  @Resource
  TemplateEngine templateEngine;

  private static final String PARAMETER = "param";

  private static final String VERIFY = "verify";

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {

    JsonReader reader = Json.createReader(new StringReader("{" + testStep.getValue() + "}"));
    JsonObject obj = reader.readObject();
    JsonObject paramObj = obj.getJsonObject(PARAMETER);
    JsonObject verifyObj = obj.getJsonObject(VERIFY);
    reader.close();

    String verifySql = writeSqlToString(testStep.getLocator().getValue()).toUpperCase();
    Map<String, String> paramMap = newUppercaseKeyMap(paramObj);

    Map<String, String> result = runVerifySql(verifySql, paramMap);

    Map<String, String> verifyMap = newUppercaseKeyMap(verifyObj);
    setUpLogInfo(verifyMap, result, verifySql, paramMap);

    String startLog = buildLog();
    ctx.info("msg", startLog);

    verify();

    String errorLog = buildErrorLog();
    String resultTable = buildHtmlTable(result, verifyMap);

    if (errorLog.length() == 0) {
      replaceLog(ctx, resultTable);
    } else {
      throw new VerifyException(StringUtils.join(new String[] {errorLog, resultTable}));
    }

  }

  private void replaceLog(SeleniumOperationContext ctx, String resultTabel) {
    int currentIdx = ctx.getRecords().size() - 1;
    String currentLog = ctx.getRecords().get(currentIdx).getLog();
    String log = StringUtils.join(new String[] {currentLog, resultTabel});
    ctx.getRecords().get(currentIdx).setLog(log);
  }

  private void setUpLogInfo(Map<String, String> verifyMap, Map<String, String> result,
      String locatorSql, Map<String, String> paramMap) {

    List<VerifyObj> verifyParams = new ArrayList<>();

    for (Entry<String, String> entry : verifyMap.entrySet()) {
      VerifyObj obj = new VerifyObj();
      obj.setVerifyCol(entry.getKey());
      obj.setExpected(entry.getValue());
      obj.setActual(result.get(entry.getKey()));
      verifyParams.add(obj);
    }

    dbVerifyLog.setVerifyColList(verifyParams);
    dbVerifyLog.setVerifySql(locatorSql);
    dbVerifyLog.setVerifyParams(paramMap);

  }

  private void verify() {

    List<String> invalidList = new ArrayList<>();
    List<VerifyObj> errorList = new ArrayList<>();
    List<String> mismatchedList = new ArrayList<>();

    for (VerifyObj obj : dbVerifyLog.getVerifyColList()) {

      if (obj.getActual() == null) {
        invalidList.add(obj.getVerifyCol());
      } else if (!(obj.getActual().equals(obj.getExpected()))) {
        errorList.add(obj);
        mismatchedList.add(obj.getVerifyCol());
      }
    }

    dbVerifyLog.setInvalidCols(invalidList);
    dbVerifyLog.setVerifyErrs(errorList);
    dbVerifyLog.setMismatchedCols(mismatchedList);

  }

  private String buildLog() {

    dbVerifyLog.setTemplate("/evidence/evidence-template-dbverify-expected-list.vm");
    dbVerifyLog.setVar("expected");

    return templateEngine.writeToString(dbVerifyLog);

  }

  private String buildErrorLog() {

    dbVerifyLog.setTemplate("/evidence/evidence-template-dbverify-error-list.vm");
    dbVerifyLog.setVar("error");

    return templateEngine.writeToString(dbVerifyLog);

  }

  private String writeSqlToString(String sqlPath) {
    try {
      File sqlFile = new File(sqlPath).getAbsoluteFile();
      log.info("sql.load", sqlFile);
      return FileUtils.readFileToString(sqlFile, "UTF-8");
    } catch (IOException e) {
      throw new TestException(e);
    }
  }

  private Map<String, String> newUppercaseKeyMap(JsonObject jsonObj) {

    Map<String, String> map = new HashMap<>();

    for (Entry<String, JsonValue> entry : jsonObj.entrySet()) {
      map.put(entry.getKey().toString().toUpperCase(), jsonObj.getString(entry.getKey()));
    }
    return map;
  }

  private Map<String, String> runVerifySql(String sql, Map<String, String> paramMap) {

    Map<String, Object> tmpResult = new HashMap<>();
    try {
      log.info("sql.execute", sql, paramMap);
      tmpResult = jdbcTemplate.queryForMap(sql, paramMap);
    } catch (DataAccessException e) {
      throw new TestException(e);
    }

    Map<String, String> result = new LinkedHashMap<>();
    for (Entry<String, Object> entry : tmpResult.entrySet()) {
      result.put(entry.getKey().toUpperCase(), entry.getValue().toString());
    }

    return result;
  }

  private String buildHtmlTable(Map<String, String> result, Map<String, String> verifyMap) {

    List<String> columns = new ArrayList<>();
    List<Value> values = new ArrayList<>();

    for (Entry<String, String> entry : result.entrySet()) {
      String style = "";
      if (verifyMap.containsKey(entry.getKey())) {
        if (dbVerifyLog.getMismatchedCols().indexOf(entry.getKey()) > -1) {
          style = "mismatched";
        } else {
          style = "verified";
        }
      }
      columns.add(entry.getKey());
      values.add(new Value(entry.getValue().toString(), style));
    }
    htmlTable.setColumns(columns);
    htmlTable.setValues(values);

    return templateEngine.writeToString(htmlTable);
  }

}
