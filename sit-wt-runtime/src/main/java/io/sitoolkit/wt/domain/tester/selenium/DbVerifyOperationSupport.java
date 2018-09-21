package io.sitoolkit.wt.domain.tester.selenium;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.selenium.DbVerifyOperation;
import io.sitoolkit.wt.domain.tester.OperationSupport;

public class DbVerifyOperationSupport implements OperationSupport {

    @Override
    public boolean isDbVerify(Operation operation) {
        if (operation instanceof DbVerifyOperation) {
            return true;
        }
        return false;
    }

}
