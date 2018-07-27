package org.sitoolkit.wt.domain.testscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.util.tabledata.csv.TableDataDaoCsvImpl;
import org.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;
import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.infra.csv.CsvFileReader;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.context.ApplicationContext;

public class TestScriptDao {

    protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

    private static final String TEMPLATE_PATH = "classpath:TestScriptTemplate_"
            + Locale.getDefault().getLanguage() + ".xlsx";

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

    @Resource
    FileOverwriteChecker fileOverwriteChecker;

    @Resource
    CsvFileReader csvReader;

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
            loadCsv(testScript, loadCaseOnly);
        }

        return testScript;
    }

    private void loadCsv(TestScript testScript, boolean loadCaseOnly) {

        List<Map<String, String>> loadedData = csvReader
                .read(testScript.getScriptFile().getAbsolutePath(), loadCaseOnly);

        Map<String, String> firstRow = loadedData.iterator().next();

        firstRow.values().stream().forEachOrdered(key -> testScript.addHeader(key));

        List<String> caseNoList = firstRow.keySet().stream()
                .filter(key -> key.startsWith(testScript.getCaseNoPrefix()))
                .map(key -> StringUtils.substringAfter(key, testScript.getCaseNoPrefix()))
                .collect(Collectors.toList());

        IntStream.range(0, caseNoList.size()).forEach(index -> {
            testScript.getCaseNoMap().put(caseNoList.get(index), index);
        });

        loadedData.stream().skip(1).forEachOrdered(row -> {
            testScript.addTestStep(createTestStep(row, caseNoList));
        });
    }

    private TestStep createTestStep(Map<String, String> row, List<String> caseNoList) {
        TestStep testStep = appCtx.getBean(TestStep.class);

        TestScriptConvertUtils.loadStep(testStep, row, caseNoList);
        Operation operation = (Operation) operationConverter.convert(Operation.class,
                testStep.getOperationName());
        testStep.setOperation(operation);

        return testStep;

    }

    private void loadScript(TableDataDao dao, TestScript testScript, String sheetName,
            boolean loadCaseOnly) {
        TableData table = dao.read(testScript.getScriptFile().getAbsolutePath(), sheetName);

        RowData firstRow = table.getRows().iterator().next();
        int testDataIndex = 0;
        for (Map.Entry<String, String> entry : firstRow.getData().entrySet()) {
            testScript.addHeader(entry.getKey());
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

    public String write(String filePath, List<TestStep> testStepList, boolean overwrite) {
        return write(new File(filePath), testStepList, overwrite);
    }

    public String write(File file, List<TestStep> testStepList, boolean overwrite) {
        File dir = file.getParentFile();
        if (dir == null) {
            dir = new File(".");
        } else if (!dir.exists()) {
            dir.mkdirs();
        }

        TableDataCatalog catalog = TestScriptConvertUtils.getTableDataCatalog(testStepList);
        String fileName = sanitizeFileName(file.getName());
        file = new File(file.getParent(), fileName);

        fileOverwriteChecker.setRebuild(overwrite);

        if (file.exists()) {
            overwrite(file, catalog);
        } else {
            excelDao.write(catalog.get("TestScript"), TEMPLATE_PATH, file.getAbsolutePath(), null);
        }

        return file.getAbsolutePath();
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }

    private void overwrite(File file, TableDataCatalog catalog) {
        Path temporaryPath = null;

        try {
            temporaryPath = Files.createTempFile(Paths.get(file.getParent()), "tmp", ".xlsx");
            Files.copy(file.toPath(), temporaryPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("temporary.create", temporaryPath.toString());

            excelDao.write(catalog.get("TestScript"), temporaryPath.toString(),
                    file.getAbsolutePath(), null);

        } catch (IOException e) {
            throw new IllegalStateException(e);

        } finally {
            try {
                if (temporaryPath != null) {
                    log.info("temporary.delete", temporaryPath.toAbsolutePath());
                    Files.deleteIfExists(temporaryPath);
                }
            } catch (IOException e) {
                log.warn("temporary.delete.error", temporaryPath.toAbsolutePath());
            }
        }

    }

}
