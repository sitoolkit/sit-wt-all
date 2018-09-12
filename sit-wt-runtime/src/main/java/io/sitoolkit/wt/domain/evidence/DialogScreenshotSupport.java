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
package io.sitoolkit.wt.domain.evidence;

import java.util.List;

import io.sitoolkit.wt.domain.testscript.TestStep;

/**
 * ダイアログのスクリーンショットを取得するための補助クラスです。
 * @author yuichi.kuwahara
 */
public interface DialogScreenshotSupport {

    /**
     * ウィンドウサイズの事前取得が必要なテストステップインデックスを確認します。
     * @param testSteps テストステップ
     * @param caseNo ケース番号
     */
    void checkReserve(List<TestStep> testSteps, String caseNo);

    /**
     * ウィンドウの位置、サイズを取得し、テストコンテキスに格納します。
     * @param testStepNo テストステップNo
     */
    public void reserveWindowRect(String testStepNo);

}
