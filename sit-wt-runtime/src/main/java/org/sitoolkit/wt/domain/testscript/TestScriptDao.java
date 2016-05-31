package org.sitoolkit.wt.domain.testscript;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.util.tabledata.csv.TableDataDaoCsvImpl;
import org.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

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
    TableDataDaoExcelImpl excelDao;

    @Resource
    TableDataDaoCsvImpl csvDao;

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
            loadScript(excelDao, testScript, testScript.getSheetName(), loadCaseOnly);
        } else if (scriptFileName.endsWith(".csv")) {
            loadScript(csvDao, testScript, "", loadCaseOnly);
        }

        return testScript;
    }

    private void loadScript(TableDataDao dao, TestScript testScript, String sheetName,
            boolean loadCaseOnly) {
        TableData table = dao.read(testScript.getScriptFile().getAbsolutePath(), sheetName);

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

        excelDao.write(catalog.get("TestScript"), TEMPLATE_PATH, file.getAbsolutePath(), null);
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }

}
