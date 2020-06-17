package io.sitoolkit.wt.domain.operation;

import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.evidence.LogRecord;
import io.sitoolkit.wt.domain.evidence.MessagePattern;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

@Component
public class CommentOperation implements Operation {

  protected SitLogger log = SitLoggerFactory.getLogger(getClass());

  @Override
  public OperationResult operate(TestStep testStep) {
    OperationResult result = new OperationResult();

    result.addRecord(LogRecord.info(log, testStep, "msg", testStep.getValue()));

    return result;
  }
}
