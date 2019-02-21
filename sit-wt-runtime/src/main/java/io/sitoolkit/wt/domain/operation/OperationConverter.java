package io.sitoolkit.wt.domain.operation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

public abstract class OperationConverter {

    @Resource
    ApplicationContext appCtx;

    public abstract Operation convert(String name);

    public abstract List<String> getOperationNames();

    protected Operation convertByPackage(String operationName, String... packageNames) {
        return Stream.of(packageNames).map(p -> OperationCatalog.get(p, operationName))
                .filter(Objects::nonNull).map(beanName -> (Operation) appCtx.getBean(beanName))
                .findFirst().get();
    }

    protected List<String> getOperationNamesByPackage(String... packages) {
        return OperationCatalog.getOperationNames(packages);
    }

}
