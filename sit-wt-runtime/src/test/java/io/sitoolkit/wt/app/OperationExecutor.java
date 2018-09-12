package io.sitoolkit.wt.app;

import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;

public class OperationExecutor {

    public static void execute(ApplicationContext appCtx, String operationName, TestStep testStep,
            Locator locator) {
        testStep.setLocator(locator);
        TestContext ctx = appCtx.getBean(TestContext.class);
        ctx.setTestStep(testStep);

        Operation operation = appCtx.getBean(operationName + "Operation", Operation.class);
        operation.operate(testStep);
    }

    public static void execute(ApplicationContext appCtx, String operationName,
            String locatorValue) {
        TestStep testStep = appCtx.getBean(TestStep.class);
        Locator locator = appCtx.getBean(Locator.class);
        locator.setValue(locatorValue);
        execute(appCtx, operationName, testStep, locator);
    }
}
