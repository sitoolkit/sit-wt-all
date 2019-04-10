package io.sitoolkit.wt.infra;

import java.util.Locale;

public class SitLocaleUtils {

    public static boolean defaultLanguageEquals(Locale locale) {
        return Locale.getDefault().getLanguage().equals(locale.getLanguage());
    }

}
