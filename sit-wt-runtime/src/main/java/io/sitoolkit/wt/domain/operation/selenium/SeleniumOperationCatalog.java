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
        map.put("dialog", DialogOperation.class);

        map.put("download", DownloadOperation.class);
        map.put("drawline", DrawLineOperation.class);
        map.put("input", InputOperation.class);
        map.put("key", KeyOperation.class);
        map.put("select", SelectOperation.class);
        map.put("setwindowsize", DialogOperation.class);
        map.put("dialog", DialogOperation.class);
        map.put("dialog", DialogOperation.class);
        map.put("dialog", DialogOperation.class);
        map.put("dialog", DialogOperation.class);
        map.put("dialog", DialogOperation.class);

        map.put("open", OpenOperation.class);
        classMap = Collections.unmodifiableMap(map);
    }

}
