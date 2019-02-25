package io.sitoolkit.wt.domain.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class OperationCatalog {

    private static final String DEFAULT_PKG = "default";

    /**
     * { key: packageName => { key: operationName => beanName } }
     */
    private static final Map<String, Map<String, String>> packageBeanMap;

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

        Pattern pattern = Pattern.compile(rootPackage + "(\\.(.*)|)\\..*");

        for (String beanName : beans) {
            BeanDefinition definition = registry.getBeanDefinition(beanName);
            Matcher matcher = pattern.matcher(definition.getBeanClassName());
            matcher.matches();
            String pkg = matcher.group(2);

            if (pkg == null) {
                pkg = DEFAULT_PKG;
            }

            String operationName = beanName.substring(0, beanName.length() - "Operation".length());

            Map<String, String> nameMap = map.computeIfAbsent(pkg, key -> new HashMap<>());

            nameMap.put(operationName, beanName);
        }

        packageBeanMap = Collections.unmodifiableMap(map);
    }

    public static String getBeanName(String operationName, String... packages) {
        return buildPackagesStream(packages).map(p -> packageBeanMap.get(p).get(operationName))
                .filter(Objects::nonNull).findFirst().get();
    }

    public static List<String> getOperationNames(String... packages) {
        return buildPackagesStream(packages).map(packageBeanMap::get).map(Map::keySet)
                .flatMap(Collection::stream).sorted().collect(Collectors.toList());
    }

    private static Stream<String> buildPackagesStream(String... packages) {
        return Stream.concat(Arrays.asList(packages).stream(), Stream.of(DEFAULT_PKG));
    }

}
