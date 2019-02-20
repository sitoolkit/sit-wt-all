package io.sitoolkit.wt.domain.operation.selenium;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.OperationCatalog;
import io.sitoolkit.wt.domain.operation.OperationConverter;

public class SeleniumOperationConverter implements OperationConverter {

    @Resource
    ApplicationContext appCtx;

    @Override
    public Operation convert(String name) {
        Class<? extends Operation> clazz = SeleniumOperationCatalog.classMap.get(name);

        if (clazz == null) {
            clazz = OperationCatalog.classMap.get(name);
        }

        return appCtx.getBean(clazz);
    }

    @Override
    public List<String> getOperationNames() {
        return Stream
                .concat(OperationCatalog.classMap.keySet().stream(),
                        SeleniumOperationCatalog.classMap.keySet().stream())
                .distinct().sorted().collect(Collectors.toList());
    }

}
