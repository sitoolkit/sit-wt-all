package org.sitoolkit.wt.util.infra.util;

import java.util.Collection;

public class StrUtils {

    public static String join(Collection<? extends Object> objects) {

        if (objects == null || objects.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Object str : objects) {

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(str);

        }

        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    public static boolean endsWithAny(String str, String... suffixes) {

        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return true;
            }
        }

        return false;
    }
}
