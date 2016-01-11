package org.sitoolkit.wt.domain.testscript;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TestScriptDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String TEMPLATE_PATH = "classpath:TestScriptTemplate.xlsx";

    @Resource
    ApplicationContext appCtx;

    @Resource
    OperationConverter operationConverter;

    @Resource
    TableDataMapper dm;

    @Resource
    TableDataDao tdDao;

    public TestScript load(String scriptPath, String sheetName, boolean loadCaseOnly) {
        return load(new File(scriptPath), sheetName, loadCaseOnly);
    }

    public TestScript load(File scriptFile, String sheetName, boolean loadCaseOnly) {
        TestScript testScript = appCtx.getBean(TestScript.class);

        testScript.setSheetName(sheetName);
        testScript.setScriptFile(scriptFile);
        testScript.setLastModified(scriptFile.lastModified());
        testScript.setName(scriptFile.getName());

        String scriptFileName = testScript.getName();

        if (scriptFileName.endsWith(".xlsx") || scriptFileName.endsWith(".xls")) {
            loadScriptFromExcel(testScript, testScript.getSheetName(), loadCaseOnly);
        } else if (scriptFileName.endsWith(".csv")) {
            loadScriptFromCsv(testScript, loadCaseOnly);
        }

        return testScript;
    }

    public void loadScriptFromExcel(TestScript testScript, String sheetName, boolean loadCaseOnly) {
        TableData table = tdDao.read(testScript.getScriptFile().getAbsolutePath(), sheetName);

        RowData firstRow = table.getRows().iterator().next();
        int testDataIndex = 0;
        for (Map.Entry<String, String> entry : firstRow.getData().entrySet()) {
            if (entry.getKey().startsWith(testScript.getCaseNoPrefix())) {
                String caseNo = StringUtils.substringAfter(entry.getKey(),
                        testScript.getCaseNoPrefix());
                testScript.getCaseNoMap().put(caseNo, testDataIndex++);
            }
        }

        if (loadCaseOnly) {
            return;
        }

        for (RowData row : table.getRows()) {
            TestStep testStep = dm.map("testStep", row, TestStep.class);
            testScript.addTestStep(testStep);
        }
    }

    public void loadScriptFromCsv(TestScript testScript, boolean loadCaseOnly) {
        List<String> lineList = null;
        try {
            lineList = FileUtils.readLines(testScript.getScriptFile(), "UTF-8");
        } catch (IOException e) {
            throw new TestException(e);
        }
        loadHeader(testScript, lineList.get(0));

        if (loadCaseOnly) {
            return;
        }

        for (String line : lineList.subList(1, lineList.size())) {
            if (line.startsWith("#")) {
                log.info("テストステップを除外します。{}", line);
                continue;
            }
            String[] values = splitLine(line);

            log.info("テストステップを追加します。{}", Arrays.toString(values));

            TestStep testStep = appCtx.getBean(TestStep.class);
            testStep.setNo(CsvColumn.no.get(values));
            testStep.setItemName(CsvColumn.itemName.get(values));
            testStep.setOperation(
                    (Operation) operationConverter.convert(null, CsvColumn.operation.get(values)));
            testStep.setLocator(Locator.build(CsvColumn.locatorType.get(values),
                    CsvColumn.locator.get(values)));
            testStep.setDataType(CsvColumn.dataType.get(values));
            testStep.setScreenshotTiming(CsvColumn.screenshotTiming.get(values));

            Map<String, String> testDataMap = new HashMap<String, String>();
            String[] testData = ArrayUtils.subarray(values, testScript.getCaseNoColIndex(),
                    values.length);
            for (Map.Entry<String, Integer> entry : testScript.getCaseNoMap().entrySet()) {
                String value = entry.getValue() < testData.length ? testData[entry.getValue()]
                        : StringUtils.EMPTY;
                testDataMap.put(entry.getKey(), value);
            }
            log.debug("テストデータを追加します。{}", testDataMap);
            testStep.setTestData(testDataMap);

            testScript.addTestStep(testStep);
        }
    }

    enum CsvColumn {
        no(0), itemName(1), operation(2), locatorType(3), locator(4), dataType(5), screenshotTiming(
                6);
        private int idx;

        private CsvColumn(int idx) {
            this.idx = idx;
        }

        public int getIdx() {
            return idx;
        }

        public String get(String[] values) {
            return values.length <= getIdx() ? StringUtils.EMPTY : values[getIdx()];
        }
    }

    /**
     * テストスクリプトのヘッダー行から ケース番号とテストデータインデックスの対応を読み込み、 ケース番号マップに格納します。
     *
     * @param header
     *            ヘッダー行の文字列
     */
    void loadHeader(TestScript testScript, String header) {
        String[] cells = splitLine(header);

        for (int i = 0; i < cells.length; i++) {
            if (cells[i].startsWith(testScript.getCaseNoPrefix())) {
                testScript.setCaseNoColIndex(i);
                break;
            }
        }

        int testDataIndex = 0;
        for (String cell : ArrayUtils.subarray(cells, testScript.getCaseNoColIndex(),
                cells.length)) {
            String caseNo = StringUtils.substringAfter(cell, testScript.getCaseNoPrefix());
            testScript.getCaseNoMap().put(caseNo, testDataIndex++);
        }

        log.info("{}件のケースを読み込みます。{}", testScript.getCaseNoMap().size(),
                testScript.getCaseNoMap().keySet());
    }

    /**
     * 文字列を「,」(半角カンマ)または「\t」(タブ)で分割した文字配列を返します。 分割後の1要素の文字列が「
     * "」(半角ダブルクオーテーション)で開始かつ終了していた場合は、 前後の「"」を除きます。
     *
     * @param line
     *            分割対象の文字列
     * @return 「,」(半角カンマ)または「\t」(タブ)で分割した文字配列
     */
    String[] splitLine(String line) {
        String[] values = line.split(",|\t");
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.startsWith("\"") && value.endsWith("\"")) {
                values[i] = value.substring(1, value.length() - 1);
            }
        }
        return values;
    }

    public void write(String filePath, List<TestStep> testStepList) {
        write(new File(filePath), testStepList);
    }

    public void write(File file, List<TestStep> testStepList) {
        File dir = file.getParentFile();
        if (dir == null) {
            dir = new File(".");
        } else if (!dir.exists()) {
            dir.mkdirs();
        }

        TableDataCatalog catalog = TestScriptConvertUtils.getTableDataCatalog(testStepList);
        String fileName = sanitizeFileName(file.getName());

        if (file.exists()) {
            String baseName = FilenameUtils.getBaseName(fileName);
            String extension = FilenameUtils.getExtension(fileName);
            fileName = baseName + "_" + System.currentTimeMillis() + "." + extension;
            file = new File(file.getParentFile(), fileName);
        }

        tdDao.write(catalog.get("TestScript"), TEMPLATE_PATH, file.getAbsolutePath(), null);
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }

}
