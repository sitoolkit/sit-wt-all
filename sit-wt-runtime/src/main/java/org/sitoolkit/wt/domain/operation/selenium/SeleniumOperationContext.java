package org.sitoolkit.wt.domain.operation.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.LogLevelVo;
import org.sitoolkit.wt.domain.evidence.LogRecord;
import org.sitoolkit.wt.domain.evidence.MessagePattern;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;

public class SeleniumOperationContext {

    private Logger logger;

    private ElementPositionSupport2 elementPositionSupport;

    private TestStep testStep;

    private List<LogRecord> records = new ArrayList<>();

    public void info(WebElement element, MessagePattern pattern, Object... params) {
        records.add(LogRecord.create(logger, conv(element), testStep, pattern, params));
    }

    public void info(WebElement element, String pattern, Object... params) {
        records.add(LogRecord.create(logger, elementPositionSupport.get(element), testStep, pattern,
                params));
    }

    public void info(String pattern, Object... params) {
        records.add(LogRecord.create(logger, LogLevelVo.INFO, testStep, pattern, params));
    }

    public void info(MessagePattern pattern, Object... params) {
        info(null, pattern, params);
    }

    private ElementPosition conv(WebElement element) {
        if (element == null) {
            return ElementPosition.EMPTY;
        }
        return elementPositionSupport.get(element);
    }

    public void addOperatedElement(List<WebElement> elements) {

        if (records.isEmpty()) {
            return;
        }

        LogRecord log = records.get(records.size() - 1);

        for (WebElement element : elements) {
            ElementPosition position = elementPositionSupport.get(element);
            if (ElementPosition.EMPTY != position) {
                position.setNo(testStep.getNo());
                log.getPositions().add(position);
            }
        }

    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public TestStep getTestStep() {
        return testStep;
    }

    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public List<LogRecord> getRecords() {
        return records;
    }

    public void setElementPositionSupport(ElementPositionSupport2 elementPositionSupport) {
        this.elementPositionSupport = elementPositionSupport;
    }

}
