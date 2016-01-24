package org.sitoolkit.wt.app;

import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.context.ApplicationContext;

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
