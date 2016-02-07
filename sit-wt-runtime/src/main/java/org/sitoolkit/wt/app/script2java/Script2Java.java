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
package org.sitoolkit.wt.app.script2java;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.app.config.ExtConfig;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.infra.SitPathUtils;
import org.sitoolkit.wt.infra.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * このクラスは、テストスクリプトを実行するためのJUnitテストクラスを生成します。
 * mainメソッドを実行すると、テストスクリプトディレクトリ以下の全テストスクリプトを読込み、
 * 対応するJUnitテストクラスのjavaファイルをテストソースディレクトリ以下に出力します。 これらのファイル、ディレクトリの位置関係を以下に示します。
 *
 * <pre>
 * {@code
 * project_root
 *   testscript                           <- テストスクリプトディレクトリ
 *     a/b/c
 *       ABCTestScript.xlsx               <- テストスクリプト
 *   target/generated-test-sources/test   <- テストソースディレクトリ
 *     a/b/c
 *       ABCTestScriptTest.java           <- JUnitテストクラス
 * }
 * </pre>
 *
 *
 * @author yuichi.kuwahara
 */
public class Script2Java implements ApplicationContextAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String SYSPROP_TESTS_SCRIPT_PATH = "testsScriptPath";

    /**
     * テストソースディレクトリのパス
     */
    private String testSrcDir = "target/generated-test-sources/test/";

    /**
     * テストスクリプトディレクトリのパス
     */
    private String testScriptDirs = "testscript,target/testscript";

    private ApplicationContext appCtx;

    private TestScriptDao dao;

    private String timestampFileName = "timestamp.xml";

    private TemplateEngine templateEngine;

    /**
     * キー：テストスクリプトのファイルパス、値：ファイルの更新日時
     */
    private Properties timestampLog = new Properties();

    public static void main(String[] args) {
        System.exit(staticExecute(null));
    }

    public static int staticExecute(String testSrcDir) {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(Script2JavaConfig.class,
                ExtConfig.class);
        Script2Java script2java = appCtx.getBean(Script2Java.class);
        script2java.setTestSrcDir(testSrcDir);

        return script2java.execute();
    }

    /**
     * テストクラスの生成処理を実行します。 システムプロパティ{@code testsScriptPath}が指定されていた場合は、
     * そこで指定されたテストスクリプトに対しテストクラスを生成します。 指定されていない場合はテストスクリプトディレクトリ以下の
     * テストスクリプトに対しテストクラスを生成します。
     *
     * @return 0 (固定)
     * @see #generate(File, String)
     */
    public int execute() {
        loadTimestampFile();

        String scriptPathes = System.getProperty(SYSPROP_TESTS_SCRIPT_PATH);
        if (StringUtils.isNotEmpty(scriptPathes)) {
            for (String path : scriptPathes.split(",")) {
                generate(new File(path), ".");
            }

        } else {
            for (String testScriptDir : testScriptDirs.split(",")) {
                File testScriptDirF = new File(testScriptDir);
                if (!testScriptDirF.exists()) {
                    continue;
                }

                log.info("テストスクリプトディレクトリ以下のスクリプトを処理します。{}", testScriptDirF.getAbsolutePath());
                for (File scriptFile : FileUtils.listFiles(testScriptDirF,
                        new String[] { "csv", "xls", "xlsx" }, true)) {
                    generate(scriptFile, testScriptDir);
                }
            }
        }

        storeTimestampFile();
        return 0;
    }

    /**
     * テストスクリプトからテストクラスを生成します。 テストスクリプトのファイル名が"~$"で始まる場合は生成処理を行いません。
     *
     * @param scriptFile
     *            テストスクリプト
     * @param testScriptDir
     */
    public void generate(File scriptFile, String testScriptDir) {

        if (scriptFile.getName().startsWith("~$")) {
            log.debug("システムファイルのため生成処理から除外します {}", scriptFile.getAbsolutePath());
            return;
        }

        String lastModified = Long.toString(scriptFile.lastModified());
        String storedLastModified = timestampLog.getProperty(scriptFile.getAbsolutePath());
        if (lastModified.equals(storedLastModified)) {
            log.info("テストスクリプトのテストクラスは最新の状態です {}", scriptFile.getAbsolutePath());
            return;
        }

        timestampLog.put(scriptFile.getAbsolutePath(), lastModified);

        log.info("テストスクリプトを読み込みます。{}", scriptFile.getAbsolutePath());

        TestClass testClass = appCtx.getBean(TestClass.class);
        load(testClass, scriptFile, testScriptDir);

        TestScript testScript = dao.load(scriptFile, testClass.getSheetName(), true);
        testClass.getCaseNos().addAll(testScript.getCaseNoMap().keySet());

        templateEngine.write(testClass);
    }

    /**
     * テストスクリプトに対応するテストクラスインスタンスを作成します。
     *
     * <dl>
     * <dt>スクリプトパス
     * <dd>スクリプトファイルのプロジェクトルートからの相対パス
     *
     * <dt>テストクラスの物理名
     * <dd>スクリプトファイルの基底名を1文字目を大文字化し末尾に"Test"を付与
     *
     * <dt>パッケージパス
     * <dd>スクリプトファイルのテストスクリプトルートからの相対パス
     *
     *
     * </dl>
     *
     *
     * @param scriptFile
     *            スクリプトファイル
     * @param testScriptDir
     * @return スクリプトファイルに対応するテストクラス
     */
    void load(TestClass testClass, File scriptFile, String testScriptDir) {

        // スクリプトパスの設定
        testClass.setScriptPath(SitPathUtils.relatvePath(new File("."), scriptFile));

        // テストクラスの物理名の設定
        String baseName = FilenameUtils.getBaseName(testClass.getScriptPath());
        testClass.setFileBase(StringUtils.capitalize(baseName) + "IT");

        // パッケージパス
        String scriptPathFromPkg = SitPathUtils.relatvePath(testScriptDir,
                testClass.getScriptPath());
        String pkgPath = FilenameUtils.getPath(scriptPathFromPkg);

        // テストクラスの出力ディレクトリの設定
        testClass.setOutDir(FilenameUtils.concat(testSrcDir, pkgPath));

        // テストクラスのパッケージ名の設定
        if (StringUtils.isEmpty(pkgPath)) {
            testClass.setPkg(null);
        } else {
            String pkg = pkgPath.replaceAll("[/|\\\\]", ".");
            pkg = StringUtils.strip(pkg, ".");
            testClass.setPkg(pkg);
        }
    }

    /**
     * タイムスタンプファイルをロードし、タイムスタンプログに保持します。
     */
    private void loadTimestampFile() {
        File timestampFile = new File(testSrcDir, timestampFileName);
        if (timestampFile.exists()) {
            try {
                timestampLog.loadFromXML(FileUtils.openInputStream(timestampFile));
            } catch (IOException e) {
                log.warn("タイムスタンプファイルの読み込みに失敗しました。", e);
            }
        }
    }

    private void storeTimestampFile() {
        File timestampFile = new File(testSrcDir, timestampFileName);

        try {
            timestampLog.storeToXML(FileUtils.openOutputStream(timestampFile, false), "");
        } catch (IOException e) {
            log.warn("タイムスタンプファイルの書き込みに失敗しました。", e);
        }
    }

    public String getTestSrcDir() {
        return testSrcDir;
    }

    public void setTestSrcDir(String testSrcDir) {
        this.testSrcDir = testSrcDir;
    }

    public String getTestScriptDir() {
        return testScriptDirs;
    }

    public void setTestScriptDir(String testScriptDir) {
        this.testScriptDirs = testScriptDir;
    }

    public TestScriptDao getDao() {
        return dao;
    }

    public void setDao(TestScriptDao dao) {
        this.dao = dao;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

}
