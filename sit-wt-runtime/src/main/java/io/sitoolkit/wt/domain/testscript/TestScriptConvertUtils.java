/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    /**
     * テストスクリプトが定義されたシート名
     */
    private static String sheetName = "TestScript";

    private static String stepNo = "StepNo";

    private static String itemName = "ItemName";

    private static String operation = "Operation";

    private static String locatorStyle = "LocatorStyle";

    private static String locator = "Locator";

    private static String dataStyle = "DataStyle";

    private static String screenshot = "Screenshot";

    private static String breakpoint = "Breakpoint";

    private static String case_ = "Case";

    public TestScriptConvertUtils() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    private static void initCellNameMap() {
        cellNameMap = new HashMap<String, String>();

        cellNameMap.put(stepNo, MessageManager.getMessage(stepNo));
        cellNameMap.put(itemName, MessageManager.getMessage(itemName));
        cellNameMap.put(operation, MessageManager.getMessage(operation));
        cellNameMap.put(locatorStyle, MessageManager.getMessage(locatorStyle));
        cellNameMap.put(locator, MessageManager.getMessage(locator));
        cellNameMap.put(dataStyle, MessageManager.getMessage(dataStyle));
        cellNameMap.put(screenshot, MessageManager.getMessage(screenshot));
        cellNameMap.put(breakpoint, MessageManager.getMessage(breakpoint));
        cellNameMap.put(case_, MessageManager.getMessage(case_));
    }

    private static String getValue(Map<String, String> row, String key) {
        if (cellNameMap == null) {
            initCellNameMap();
        }
        return row.get(cellNameMap.get(key));
    }

    public static void loadStep(TestStep testStep, Map<String, String> row,
            List<String> caseNoList) {

        testStep.setNo(getValue(row, stepNo));
        testStep.setItemName(getValue(row, itemName));
        testStep.setOperationName(getValue(row, operation));
        testStep.getLocator().setType(getValue(row, locatorStyle));
        testStep.getLocator().setValue(getValue(row, locator));
        testStep.setDataType(getValue(row, dataStyle));
        testStep.setScreenshotTiming(getValue(row, screenshot));
        testStep.setBreakPoint(getValue(row, breakpoint));
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

        Stream<String> itemStream = Stream.of(stepNo, itemName, operation, locatorStyle, locator,
                dataStyle, screenshot, breakpoint).map(cellNameMap::get);
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
        row.add(testStep.getScreenshotTiming());
        row.add(testStep.getBreakPoint());
        caseNoList.stream().map(testStep.getTestData()::get).forEachOrdered(row::add);
        return row;
    }

}
