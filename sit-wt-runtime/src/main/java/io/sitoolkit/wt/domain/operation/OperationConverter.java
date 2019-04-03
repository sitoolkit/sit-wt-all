package io.sitoolkit.wt.domain.operation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class OperationConverter {

    @Resource
    ApplicationContext appCtx;

    /**
     * key: operationName, value: beanName
     */
    private static final Map<String, String> beanMap;

    static {
        Map<String, String> map = new HashMap<>();

        BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

        TypeFilter tf = new AssignableTypeFilter(Operation.class);
        scanner.setIncludeAnnotationConfig(false);
        scanner.addIncludeFilter(tf);
        scanner.scan(OperationConverter.class.getPackage().getName());
        String[] beanNames = registry.getBeanDefinitionNames();

        final int suffixLength = "Operation".length();
        for (String beanName : beanNames) {
            String operationName = beanName.substring(0, beanName.length() - suffixLength);
            map.put(operationName, beanName);
        }

        beanMap = Collections.unmodifiableMap(map);
    }

    public Optional<Operation> convert(String operationName) {
        String beanName = beanMap.get(operationName);
        if (StringUtils.isEmpty(beanName)) {
            return Optional.empty();
        }

        return Optional.of((Operation) appCtx.getBean(beanMap.get(operationName)));
    }

    public List<String> getOperationNames() {
        return beanMap.keySet().stream().sorted().collect(Collectors.toList());
    }

}
