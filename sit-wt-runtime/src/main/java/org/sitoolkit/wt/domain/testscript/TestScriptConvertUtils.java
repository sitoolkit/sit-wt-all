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
package org.sitoolkit.wt.domain.testscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;
import org.sitoolkit.wt.infra.resource.MessageManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(xlsx)に変換するクラスです。
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

    /**
     * TestScriptオブジェクトをRowDataオブジェクトに変換して TableDataCatalogに格納します。
     *
     * @param testStepList
     * @return
     */
    public static TableDataCatalog getTableDataCatalog(List<TestStep> testStepList,
            List<String> headers) {
        if (cellNameMap == null) {
            initCellNameMap();
        }
        TableDataCatalog tableDataCatalog = new TableDataCatalog();
        TableData tableData = new TableData();

        for (TestStep testStep : testStepList) {
            RowData row = new RowData();

            row.setCellValue("No.", testStep.getNo());
            row.setCellValue(cellNameMap.get(itemName), testStep.getItemName());
            row.setCellValue(cellNameMap.get(operation), testStep.getOperationName());
            if (!Locator.Type.na.equals(testStep.getLocator().getType())) {
                row.setCellValue(cellNameMap.get(locatorStyle), testStep.getLocator().getType());
            }
            row.setCellValue(cellNameMap.get(locator), testStep.getLocator().getValue());
            row.setCellValue(cellNameMap.get(dataStyle), testStep.getDataType());
            row.setCellValue(cellNameMap.get(screenshot), testStep.getScreenshotTiming());
            row.setCellValue(cellNameMap.get(breakpoint), testStep.getBreakPoint());
            
            for (Entry<String, String> entry : testStep.getTestData().entrySet()) {
                row.setCellValue(cellNameMap.get(case_) + entry.getKey(), entry.getValue());
            }
            tableData.add(row);

        }

        tableData.setName(sheetName);
        tableData.setColumnNameList(headers);
        tableDataCatalog.add(tableData);

        return tableDataCatalog;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    private static void initCellNameMap() {
        cellNameMap = new HashMap<String, String>();

        cellNameMap.put(itemName, MessageManager.getMessage(itemName));
        cellNameMap.put(operation, MessageManager.getMessage(operation));
        cellNameMap.put(locatorStyle, MessageManager.getMessage(locatorStyle));
        cellNameMap.put(locator, MessageManager.getMessage(locator));
        cellNameMap.put(dataStyle, MessageManager.getMessage(dataStyle));
        cellNameMap.put(screenshot, MessageManager.getMessage(screenshot));
        cellNameMap.put(breakpoint, MessageManager.getMessage(breakpoint));
        cellNameMap.put(case_, MessageManager.getMessage(case_));
    }

}
