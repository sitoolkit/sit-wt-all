package io.sitoolkit.wt.domain.operation.selenium;

import java.util.List;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.OperationConverter;

public class SeleniumOperationConverter extends OperationConverter {

    private static final String OPERATION_PACKAGE = "selenium";

    @Override
    public Operation convert(String operationName) {
        return convertByPackage(operationName, OPERATION_PACKAGE);
    }

    @Override
    public List<String> getOperationNames() {
        return getOperationNamesByPackage(OPERATION_PACKAGE);
    }

}
