package org.sitoolkit.wt.gui.infra;

import java.util.Collection;

public class StrUtils {

    public static String join(Collection<String> strs) {

        if (strs == null || strs.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String str : strs) {

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

}
