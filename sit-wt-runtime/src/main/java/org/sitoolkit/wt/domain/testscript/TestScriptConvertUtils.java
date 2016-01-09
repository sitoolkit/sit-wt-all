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

import java.util.List;
import java.util.Map.Entry;

import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;

/**
 * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(xlsx)に変換するクラスです。
 *
 * @author yuichi.kuwahara
 * @author kaori.ogawa
 */
public class TestScriptConvertUtils {

    /**
     * テストスクリプトが定義されたシート名
     */
    private static String sheetName = "TestScript";

    public TestScriptConvertUtils() {
    }

    /**
     * TestScriptオブジェクトをRowDataオブジェクトに変換して TableDataCatalogに格納します。
     * 
     * @param testStepList
     * @return
     */
    public static TableDataCatalog getTableDataCatalog(List<TestStep> testStepList) {
        TableDataCatalog tableDataCatalog = new TableDataCatalog();
        TableData tableData = new TableData();

        for (TestStep testStep : testStepList) {
            RowData row = new RowData();

            row.setCellValue("No.", testStep.getNo());
            row.setCellValue("項目名", testStep.getItemName());
            row.setCellValue("操作", testStep.getOperationName());
            if (!Locator.Type.na.equals(testStep.getLocator().getType())) {
                row.setCellValue("ロケーター形式", testStep.getLocator().getType());
            }
            row.setCellValue("ロケーター", testStep.getLocator().getValue());
            row.setCellValue("データ形式", testStep.getDataType());
            row.setCellValue("スクリーンショット", testStep.getScreenshotTiming());

            for (Entry<String, String> entry : testStep.getTestData().entrySet()) {
                row.setCellValue("ケース_" + entry.getKey(), entry.getValue());
            }
            tableData.add(row);
        }

        tableData.setName(sheetName);
        tableDataCatalog.add(tableData);

        return tableDataCatalog;
    }
}
