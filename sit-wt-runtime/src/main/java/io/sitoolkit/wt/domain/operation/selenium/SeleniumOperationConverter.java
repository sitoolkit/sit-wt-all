package io.sitoolkit.wt.domain.operation.selenium;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.OperationConverter;

public class SeleniumOperationConverter extends OperationConverter {

    private static String[] OPERATION_PACKAGES = new String[] { "selenium", "default" };

    @Resource
    ApplicationContext appCtx;

    @Override
    public Operation convert(String operationName) {
        return convertByPackage(operationName, OPERATION_PACKAGES);
    }

    @Override
    public List<String> getOperationNames() {
        return getOperationNamesByPackage(OPERATION_PACKAGES);
    }

}
