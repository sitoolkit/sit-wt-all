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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.infra.ELSupport;

/**
 *
 * @author yuichi.kuwahara
 */
public class TestStep {

    public static final List<String> SCREENSHOT_TIMING_VALUES = Collections
            .unmodifiableList(Arrays.asList("前", "後"));

    @Resource
    ELSupport el;
    /**
     * ステップNo
     */
    private String no;
    /**
     * 項目名
     */
    private String itemName;
    /**
     * 操作
     */
    private Operation operation;
    /**
     * 操作名
     */
    private String operationName;
    /**
     * 操作対象の項目を特定するロケーター
     */
    @Resource
    private Locator locator;
    /**
     * スクリーンショットを撮るタイミング
     */
    private String screenshotTiming;
    /**
     * テストデータの形式
     */
    private String dataType;
    /**
     * 操作対象の項目に適用するテストデータ
     */
    private Map<String, String> testData = new HashMap<>();
    private String currentCaseNo;

    /**
     * ダイアログのスクリーンショットを取得するケース番号
     */
    private Set<String> dialogScreenshotCaseNoSet = new HashSet<String>();

    /**
     * ブレークポイント
     */
    private String breakPoint;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    /**
     * 操作名を取得します。
     *
     * @return 操作名
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * 操作名を設定します。
     *
     * @param operationName
     *            操作名
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * ロケーターを取得します。
     *
     * @return ロケーター ブランクの場合は項目名
     */
    public Locator getLocator() {
        return locator;
    }

    /**
     * ロケーターを設定します。
     *
     * @param locator
     *            ロケーター
     */
    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * テストデータの中から実行中ケースで使用する値を取得します。
     *
     * @return 実行中ケースで使用する値
     */
    public String getValue() {
        String value = getTestData().get(getCurrentCaseNo());
        return el.evaluate(value);
    }

    public String[] getValues() {
        return StringUtils.isBlank(getValue()) ? new String[0] : getValue().split("(:|;)");
    }

    public boolean getDialogValue() {
        String v = getValue().toLowerCase();
        return "true".equalsIgnoreCase(v) || "ok".equalsIgnoreCase(v) || "y".equalsIgnoreCase(v);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Map<String, String> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, String> testData) {
        this.testData = testData;
    }

    public void setTestData(String caseNo, String testData) {
        this.testData.put(caseNo, testData);
    }

    public String getScreenshotTiming() {
        return screenshotTiming;
    }

    public void setScreenshotTiming(String screenshotTiming) {
        this.screenshotTiming = screenshotTiming;
    }

    /**
     * 操作実行前にスクリーンショットが必要な場合にtrueを返します。
     *
     * @return 操作実行前にスクリーンショットが必要な場合にtrue
     */
    public boolean beforeScreenshot() {
        return StringUtils.contains(getScreenshotTiming(), "前");
    }

    /**
     * 操作実行後にスクリーンショットが必要な場合にtrueを返します。
     *
     * @return 操作実行後にスクリーンショットが必要な場合にtrue
     */
    public boolean afterScreenshot() {
        return StringUtils.contains(getScreenshotTiming(), "後");
    }

    /**
     * 現在のケースでダイアログのスクリーンショットが必要な場合にtrueを返します。
     *
     * @return 現在のケースでダイアログのスクリーンショットが必要な場合にtrue
     */
    public boolean dialogScreenshot() {
        return dialogScreenshotCaseNoSet.contains(getCurrentCaseNo());
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = StringUtils.lowerCase(dataType);
    }

    /**
     * 実行中ケースでは当該操作を行わないことを表します。 テスト値が空の場合にtrueとなります。
     *
     * @return
     */
    public boolean isSkip() {
        return StringUtils.isBlank(getValue());
    }

    public boolean isCaseStrExists() {
        return StringUtils.isNotBlank(getTestData().get(getCurrentCaseNo()));
    }

    public String getCurrentCaseNo() {
        return currentCaseNo;
    }

    public void setCurrentCaseNo(String currentCaseNo) {
        this.currentCaseNo = currentCaseNo;
    }

    public void addDialogScreenshotCaseNo(String caseNo) {
        dialogScreenshotCaseNoSet.add(caseNo);
    }

    public String getBreakPoint() {
        return breakPoint;
    }

    public void setBreakPoint(String breakPoint) {
        this.breakPoint = breakPoint;
    }

    public boolean isBreakPointEnabled() {
        return StringUtils.isNotEmpty(getBreakPoint());
    }
}
