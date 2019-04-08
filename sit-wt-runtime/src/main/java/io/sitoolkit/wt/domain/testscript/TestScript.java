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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;

/**
 *
 * @author yuichi.kuwahara
 */
public class TestScript {

    protected final SitLogger log = SitLoggerFactory.getLogger(getClass());
    /**
     * テストスクリプトの中から ケース番号のカラムを識別するためのプレフィックス
     */
    private String caseNoPrefix = "";
    /**
     * テストスクリプト内で ケースのテストデータが記載された列番号
     */
    private int caseNoColIndex;
    /**
     * テストステップ
     */
    private List<TestStep> testStepList = new ArrayList<TestStep>();
    /**
     * テストスクリプト内の ヘッダー文字列のリスト
     */
    private List<String> headers = new ArrayList<>();
    /**
     * キー：ステップNo、値：ステップNoを持つTestScriptのtestStepList内のインデックス
     */
    private Map<String, Integer> testStepNoMap = new HashMap<String, Integer>();
    /**
     * ケース番号マップ キー：ケース番号、値：テストデータ配列のインデックス
     */
    private Map<String, Integer> caseNoMap = new HashMap<String, Integer>();

    /**
     * テストスクリプトの名前
     */
    private String name;

    /**
     * テストスクリプトファイルが最後に更新された日時
     */
    private long lastModified;

    /**
     * テストスクリプトのファイル
     */
    private File scriptFile;

    private String sheetName;

    public String getCaseNoPrefix() {
        if (caseNoPrefix == "") {
            setCaseNoPrefix();
        }
        return caseNoPrefix;
    }

    public void setCaseNoPrefix(String caseNoPrefix) {
        this.caseNoPrefix = caseNoPrefix;
    }

    private void setCaseNoPrefix() {
        this.caseNoPrefix = MessageManager.getMessage("testScript-header-caseNoPrefix");
    }

    public List<TestStep> getTestStepList() {
        return Collections.unmodifiableList(testStepList);
    }

    public void addTestStep(TestStep testStep) {
        if (testStep != null && testStep.getOperation() != null) {
            copyLocator(testStepList, testStep);
            testStepList.add(testStep);
            testStepNoMap.put(testStep.getNo(), testStepList.size() - 1);
        }
    }

    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public TestStep getTestStep(int index) {
        try {
            return testStepList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 当該テストスクリプト内のテストステップ数を取得します。
     *
     * @return 当該テストスクリプト内のテストステップ数
     */
    public int getTestStepCount() {
        return testStepList.size();
    }

    @Deprecated
    public Integer getScriptIndex(String testStepNo) {
        return testStepNoMap.get(testStepNo);
    }

    public void setTestStepList(List<TestStep> testStepList) {
        this.testStepList = testStepList;
    }

    public Map<String, Integer> getCaseNoMap() {
        return caseNoMap;
    }

    public void setCaseNoMap(Map<String, Integer> caseNoMap) {
        this.caseNoMap = caseNoMap;
    }

    public boolean containsCaseNo(String caseNo) {
        return caseNoMap.containsKey(caseNo);
    }

    public boolean isScriptFileChanged() {
        return lastModified != scriptFile.lastModified();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndexByScriptNo(String no) {
        Integer idx = testStepNoMap.get(no);
        return idx == null ? -1 : idx;
    }

    public String getSteptNo(int index) {
        TestStep testStep = getTestStep(index);
        return testStep == null ? null : testStep.getNo();
    }

    /**
     * リスト内にコピー先テストステップと同じ項目名を持つものが存在する場合、 ロケーターをコピーします。
     *
     * コピーは、コピー先テストステップのロケーターが空である場合に行います。 ロケーターが空でない場合、または同じ項目名を持つものが存在しない場合は
     * このメソッドは何も行いません。
     *
     * @param list
     *            リスト
     * @param testStep
     *            コピー先テストステップ
     */
    private void copyLocator(List<TestStep> list, TestStep testStep) {
        String itemName = testStep.getItemName();

        for (TestStep ts : list) {
            if (itemName.equals(ts.getItemName())) {

                if (testStep.getLocator().isEmpty()) {
                    testStep.setLocator(ts.getLocator());
                }
            }
        }
    }

    public TestStep getLastStep() {
        return testStepList.get(testStepList.size() - 1);
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getCaseNoColIndex() {
        return caseNoColIndex;
    }

    public void setCaseNoColIndex(int caseNoColIndex) {
        this.caseNoColIndex = caseNoColIndex;
    }
}
