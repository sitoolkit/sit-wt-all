package org.sitoolkit.wt.domain.tester.selenium;

import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.domain.operation.selenium.DbVerifyOperation;
import org.sitoolkit.wt.domain.tester.OperationSupport;

public class DbVerifyOperationSupport implements OperationSupport {

    @Override
    public boolean isDbVerify(Operation operation) {
        if (operation instanceof DbVerifyOperation) {
            return true;
        }
        return false;
    }

}
