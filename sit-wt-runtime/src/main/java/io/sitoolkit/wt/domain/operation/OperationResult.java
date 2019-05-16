package io.sitoolkit.wt.domain.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.sitoolkit.wt.domain.evidence.LogRecord;

public class OperationResult {

  private List<LogRecord> records = new ArrayList<>();

  public OperationResult() {
    super();
  }

  public OperationResult(LogRecord... logRecords) {
    this();
    this.records = Arrays.asList(logRecords);
  }

  public OperationResult(List<LogRecord> logRecords) {
    this();
    this.records = logRecords;
  }

  public void addRecord(LogRecord record) {
    records.add(record);
  }

  public List<LogRecord> getRecords() {
    return records;
  }

  public void setRecords(List<LogRecord> records) {
    this.records = records;
  }

}
