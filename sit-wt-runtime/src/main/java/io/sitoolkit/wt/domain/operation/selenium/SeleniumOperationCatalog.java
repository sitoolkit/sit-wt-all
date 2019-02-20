package io.sitoolkit.wt.domain.operation.selenium;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.sitoolkit.wt.domain.operation.Operation;

public class SeleniumOperationCatalog {

    /**
     * key: operation name
     */
    public static Map<String, Class<? extends Operation>> classMap;

    static {
        Map<String, Class<? extends Operation>> map = new HashMap<>();
        map.put("choose", ChooseOperation.class);
        map.put("click", ClickOperation.class);
        map.put("dbverify", DbVerifyOperation.class);
        map.put("open", OpenOperation.class);
        classMap = Collections.unmodifiableMap(map);
    }

}
