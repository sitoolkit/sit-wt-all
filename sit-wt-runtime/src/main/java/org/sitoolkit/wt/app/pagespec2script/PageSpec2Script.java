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
package org.sitoolkit.wt.app.pagespec2script;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.wt.app.config.ExtConfig;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * このクラスは、画面仕様書をテストスクリプトに変換します。 mainメソッドを実行すると、画面仕様書ディレクトリ以下の全画面仕様書を読込み、
 * 対応するテストスクリプトをテストスクリプトディレクトリ以下に出力します。 これらのファイル、ディレクトリの位置関係を以下に示します。
 *
 * <pre>
 * {@code
 * project_root
 *   pagespec                       <- 画面仕様書ディレクトリ
 *     a/b/c
 *       画面仕様書_入力画面.xlsx   <- 画面仕様書
 *   pageobj                        <- テストスクリプトディレクトリ
 *     a/b/c
 *       入力画面TestScript.java    <- テストスクリプト
 * }
 * </pre>
 *
 * @author yuichi.kuwahara
 */
public class PageSpec2Script implements ApplicationContextAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationContext appCtx;

    private IOFileFilter fileFilter;

    private TestScriptDao dao;

    private TableDataMapper tdm;

    private TableDataDao tableDataDao;

    private Map<String, String> pageSpecConverterMap;

    private String pagespecDir = "pagespec";

    private String testScriptDir = "pageobj";

    private String caseNo = "001";

    private String sheetName = "項目定義";

    public static void main(String[] args) {
        PageSpec2Script converter = initInstance();
        System.exit(converter.execute());
    }

    public static PageSpec2Script initInstance() {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                PageSpec2ScriptConfig.class, ExtConfig.class);
        return appCtx.getBean(PageSpec2Script.class);
    }

    public int execute() {
        File pagespecDirF = new File(pagespecDir);

        if (!pagespecDirF.exists()) {
            log.info("画面仕様書ディレクトリを作成します。{}", pagespecDirF.getAbsolutePath());
            pagespecDirF.mkdirs();
        }

        log.info("画面仕様書ディレクトリ以下のスクリプトを処理します。{}", pagespecDirF.getAbsolutePath());
        for (File scriptFile : FileUtils.listFiles(pagespecDirF, fileFilter,
                DirectoryFileFilter.DIRECTORY)) {
            convert(scriptFile);
        }

        return 0;
    }

    /**
     * 画面定義書をテストスクリプトに変換します。
     *
     * @param pageSpec
     *            画面定義書
     * @return テストスクリプト
     */
    protected File convert(File pageSpec) {
        String pageSpecBaseName = FilenameUtils.getBaseName(pageSpec.getName());
        String testScriptName = StringUtils.substringAfter(pageSpecBaseName, "_")
                + "TestScript.xlsx";

        List<PageItemSpec> pageItemSpeclist = loadPageSpec(pageSpec.getAbsolutePath(), sheetName);

        List<TestStep> testStepList = new ArrayList<>();
        int no = 1;
        for (PageItemSpec pageItemSpec : pageItemSpeclist) {
            testStepList.add(convertPageItemSpec(no++, pageItemSpec));
        }

        File testScript = new File(testScriptDir, testScriptName);
        dao.write(testScript, testStepList, false);

        return testScript;
    }

    /**
     * 画面定義書ファイルを読み込みます。
     *
     * @param pageSpecPath
     *            画面定義書のファイルパス
     * @param sheetName
     *            項目定義のシート名
     * @return 画面定義書
     */
    protected List<PageItemSpec> loadPageSpec(String pageSpecPath, String sheetName) {

        List<PageItemSpec> pageItemSpecList = new ArrayList<PageItemSpec>();

        TableDataMapper dm = appCtx.getBean(TableDataMapper.class);

        for (RowData row : tableDataDao.read(pageSpecPath, sheetName).getRows()) {
            PageItemSpec pageItemSpec = dm.map("pageItemSpec", row, PageItemSpec.class);
            pageItemSpecList.add(pageItemSpec);
        }

        return pageItemSpecList;

    }

    /**
     * 画面仕様書の1行をテストステップに変換します。
     *
     * @param no
     *            テストステップNo
     * @param pageItemSpec
     *            画面仕様書の1行
     * @return テストステップ
     */
    protected TestStep convertPageItemSpec(int no, PageItemSpec pageItemSpec) {
        TestStep testStep = new TestStep();

        testStep.setNo(Integer.toString(no));
        testStep.setCurrentCaseNo(caseNo);

        // 項目名
        testStep.setItemName(pageItemSpec.getName());

        // ロケーター
        Locator locator = buildLocator(pageItemSpec.getName(), pageItemSpec.getPname());
        testStep.setLocator(locator);

        // 操作
        String operationName = pageSpecConverterMap.get(pageItemSpec.getControl());
        testStep.setOperationName(operationName);

        if ("click".equals(operationName)) {
            testStep.setScreenshotTiming("前");
        }

        // ケースデータ
        Map<String, String> testData = new HashMap<String, String>();
        testStep.setTestData(testData);
        // TODO クリック操作以外は入力値となるケースデータを自動判別できないためスキップさせる
        String caseData = "click".equals(operationName) ? "y" : "";
        testData.put(caseNo, caseData);

        return testStep;
    }

    protected Locator buildLocator(String name, String pName) {
        if (pName == null) {
            return Locator.build("id", name);
        } else {
            return Locator.build("id", pName);
        }
    }

    public TestScriptDao getDao() {
        return dao;
    }

    public void setDao(TestScriptDao dao) {
        this.dao = dao;
    }

    public Map<String, String> getPageSpecConverterMap() {
        return pageSpecConverterMap;
    }

    public void setPageSpecConverterMap(Map<String, String> pageSpecConverterMap) {
        this.pageSpecConverterMap = pageSpecConverterMap;
    }

    public String getPagespecDir() {
        return pagespecDir;
    }

    public void setPagespecDir(String pagespecDir) {
        this.pagespecDir = pagespecDir;
    }

    public String getTestScriptDir() {
        return testScriptDir;
    }

    public void setTestScriptDir(String testScriptDir) {
        this.testScriptDir = testScriptDir;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    public IOFileFilter getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(IOFileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public TableDataMapper getTdm() {
        return tdm;
    }

    public void setTdm(TableDataMapper tdm) {
        this.tdm = tdm;
    }

    public TableDataDao getTableDataDao() {
        return tableDataDao;
    }

    public void setTableDataDao(TableDataDao tableDataDao) {
        this.tableDataDao = tableDataDao;
    }
}
