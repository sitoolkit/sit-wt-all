package io.sitoolkit.wt.domain.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class OperationCatalog {

    /**
     * { key: packageName => { key: operationName => beanName } }
     */
    private static Map<String, Map<String, String>> beanMap;

    static {
        Map<String, Map<String, String>> map = new HashMap<>();

        BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

        String rootPackage = OperationConverter.class.getPackage().getName();

        TypeFilter tf = new AssignableTypeFilter(Operation.class);
        scanner.setIncludeAnnotationConfig(false);
        scanner.addIncludeFilter(tf);
        scanner.scan(rootPackage);
        String[] beans = registry.getBeanDefinitionNames();

        Pattern pattern = Pattern.compile(rootPackage + "(\\.(.*)|)\\..*Operation");

        for (String beanName : beans) {
            BeanDefinition definition = registry.getBeanDefinition(beanName);
            Matcher matcher = pattern.matcher(definition.getBeanClassName());
            matcher.matches();
            String pkg = matcher.group(2);

            if (pkg == null) {
                pkg = "default";
            }

            String operationName = beanName.substring(0, beanName.length() - "Operation".length());

            Map<String, String> nameMap = map.computeIfAbsent(pkg, key -> new HashMap<>());

            nameMap.put(operationName, beanName);
        }

        beanMap = Collections.unmodifiableMap(map);
    }

    public static String get(String pkg, String beanName) {
        return beanMap.get(pkg).get(beanName);
    }

    public static List<String> getOperationNames(String... packages) {
        return Arrays.asList(packages).stream().map(beanMap::get).map(Map::keySet)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

}
