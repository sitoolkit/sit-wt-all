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
package org.sitoolkit.wt.domain.evidence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * このクラスは、操作ログの構成単位となるVOです。
 * 1回の操作、または1つのスクリーンショットを1インスタンスとして保持します。
 */
public class LogRecord {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * ステップNo
     */
    private String no = "";
    /**
     * タイムスタンプ
     */
    private String timestamp;
    /**
     * ログ文字列
     */
    private String log;
    /**
     * スクリーンショットファイル
     */
    private String filePath;

    /**
     * 要素の位置情報
     */
    private List<ElementPosition> positions;

    /**
     * ログレベル
     */
    private LogLevelVo logLevel;

    public LogRecord() {
        this.timestamp = DATE_FORMAT.format(new Date());
    }

    public LogRecord(String log) {
        this();
        this.log = log;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<ElementPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<ElementPosition> positions) {
        this.positions = positions;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public LogLevelVo getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevelVo logLevel) {
        this.logLevel = logLevel;
    }

}
