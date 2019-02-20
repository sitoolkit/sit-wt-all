package io.sitoolkit.wt.domain.operation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OperationCatalog {

    /**
     * key: operation name
     */
    public static Map<String, Class<? extends Operation>> classMap;

    static {
        Map<String, Class<? extends Operation>> map = new HashMap<>();
        map.put("exec", ExecOperation.class);
        map.put("goto", GotoOperation.class);
        map.put("include", IncludeOperation.class);
        classMap = Collections.unmodifiableMap(map);
    }

}
