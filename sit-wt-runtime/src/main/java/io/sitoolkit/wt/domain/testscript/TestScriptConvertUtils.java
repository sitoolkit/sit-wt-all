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
package io.sitoolkit.wt.domain.testscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import io.sitoolkit.wt.infra.resource.MessageManager;

/**
 * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプトに変換するクラスです。
 *
 * @author yuichi.kuwahara
 * @author kaori.ogawa
 */
public class TestScriptConvertUtils implements ApplicationContextAware {

  protected ApplicationContext appCtx;

  private static Map<String, String> cellNameMap;

  private static String stepNo = "stepNo";

  private static String itemName = "itemName";

  private static String operation = "operation";

  private static String locatorStyle = "locatorStyle";

  private static String locator = "locator";

  private static String dataStyle = "dataStyle";

  private static String screenshot = "screenshot";

  private static String breakPoint = "breakPoint";

  private static String caseNoPrefix = "caseNoPrefix";

  public TestScriptConvertUtils() {}

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.appCtx = applicationContext;
  }

  private static void initCellNameMap() {
    cellNameMap = new HashMap<String, String>();

    cellNameMap.put(stepNo, getCellName(stepNo));
    cellNameMap.put(itemName, getCellName(itemName));
    cellNameMap.put(operation, getCellName(operation));
    cellNameMap.put(locatorStyle, getCellName(locatorStyle));
    cellNameMap.put(locator, getCellName(locator));
    cellNameMap.put(dataStyle, getCellName(dataStyle));
    cellNameMap.put(screenshot, getCellName(screenshot));
    cellNameMap.put(breakPoint, getCellName(breakPoint));
    cellNameMap.put(caseNoPrefix, getCellName(caseNoPrefix));
  }

  private static String getCellName(String name) {
    return MessageManager.getMessage("testScript-header-" + name);
  }

  private static String getValue(Map<String, String> row, String key) {
    if (cellNameMap == null) {
      initCellNameMap();
    }
    return row.get(cellNameMap.get(key));
  }

  public static void loadStep(TestStep testStep, Map<String, String> row, List<String> caseNoList) {

    testStep.setNo(getValue(row, stepNo));
    testStep.setItemName(getValue(row, itemName));
    testStep.setOperationName(getValue(row, operation));
    testStep.getLocator().setType(getValue(row, locatorStyle));
    testStep.getLocator().setValue(getValue(row, locator));
    testStep.setDataType(getValue(row, dataStyle));
    testStep.setScreenshotTiming(getValue(row, screenshot));
    testStep.setBreakPoint(getValue(row, breakPoint));
    Map<String, String> testData = new HashMap<String, String>();

    String casePrefix = (new TestScript()).getCaseNoPrefix();
    caseNoList.stream().forEach(s -> {
      testData.put(s, row.get(casePrefix + s));
    });
    testStep.setTestData(testData);
  }

  public static List<String> createHeaderRow(List<String> caseNoList) {
    if (cellNameMap == null) {
      initCellNameMap();
    }

    String casePrefix = (new TestScript()).getCaseNoPrefix();

    Stream<String> itemStream = Stream
        .of(stepNo, itemName, operation, locatorStyle, locator, dataStyle, screenshot, breakPoint)
        .map(cellNameMap::get);
    Stream<String> caseStream = caseNoList.stream().map(caseNo -> casePrefix + caseNo);

    return Stream.concat(itemStream, caseStream).collect(Collectors.toList());
  }

  public static List<String> createRow(TestStep testStep, List<String> caseNoList) {

    List<String> row = new ArrayList<>();

    row.add(testStep.getNo());
    row.add(testStep.getItemName());
    row.add(testStep.getOperationName());
    row.add(testStep.getLocator().getType());
    row.add(testStep.getLocator().getValue());
    row.add(testStep.getDataType());
    row.add(testStep.getScreenshotTiming().getLabel());
    row.add(testStep.getBreakPoint());
    caseNoList.stream().map(testStep.getTestData()::get).forEachOrdered(row::add);
    return row;
  }

}
