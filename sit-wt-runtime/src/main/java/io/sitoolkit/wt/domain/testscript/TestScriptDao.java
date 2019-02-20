package io.sitoolkit.wt.domain.testscript;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.OperationConverter;
import io.sitoolkit.wt.infra.csv.CsvFileReader;
import io.sitoolkit.wt.infra.csv.CsvFileWriter;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.OverwriteChecker;

public class TestScriptDao {

    protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

    @Resource
    ApplicationContext appCtx;

    @Resource
    OperationConverter operationConverter;

    @Resource
    OverwriteChecker overwriteChecker;

    @Resource
    CsvFileReader csvReader;

    @Resource
    CsvFileWriter csvWriter;

    public TestScript load(String scriptPath, String sheetName, boolean loadCaseOnly) {
        return load(new File(scriptPath), sheetName, loadCaseOnly);
    }

    public TestScript load(File scriptFile, String sheetName, boolean loadCaseOnly) {
        TestScript testScript = appCtx.getBean(TestScript.class);

        testScript.setSheetName(sheetName);
        testScript.setScriptFile(scriptFile);
        testScript.setLastModified(scriptFile.lastModified());
        testScript.setName(scriptFile.getName());

        loadCsv(testScript, loadCaseOnly);

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
        Operation operation = operationConverter.convert(testStep.getOperationName());
        testStep.setOperation(operation);
        return testStep;

    }

    public String write(String filePath, List<TestStep> testStepList, boolean overwrite) {
        return write(new File(filePath), testStepList, overwrite);
    }

    public String write(File file, List<TestStep> testStepList, boolean overwrite) {
        return write(file, testStepList, null, overwrite);
    }

    public String write(File file, List<TestStep> testStepList, List<String> headers,
            boolean overwrite) {

        writeCsv(file.toPath(), testStepList, overwrite);
        return file.getAbsolutePath();
    }

    private void writeCsv(Path path, List<TestStep> testSteps, boolean overwrite) {
        overwriteChecker.setRebuild(overwrite);
        if (!overwriteChecker.isWritable(path)) {
            return;
        }

        List<List<String>> data = createWriteData(testSteps);
        csvWriter.write(data, path.toAbsolutePath().toString());
        log.info("script.file.saved", path.toAbsolutePath().toString());
    }

    private List<List<String>> createWriteData(List<TestStep> testSteps) {
        List<String> caseNoList = getCaseNoList(testSteps);
        List<String> headerRow = TestScriptConvertUtils.createHeaderRow(caseNoList);
        List<List<String>> writeData = new ArrayList<>();
        writeData.add(headerRow);
        testSteps.stream().map(step -> TestScriptConvertUtils.createRow(step, caseNoList))
                .forEachOrdered(writeData::add);
        return writeData;
    }

    private List<String> getCaseNoList(List<TestStep> testSteps) {
        if (CollectionUtils.isEmpty(testSteps)) {
            return Collections.emptyList();

        } else {
            return new ArrayList<>(testSteps.get(0).getTestData().keySet());
        }
    }

}
