package io.sitoolkit.wt.infra;

import org.apache.commons.lang3.SystemUtils;

public class SitRepository {

    public SitRepository() {
    }

    public static String getRepositoryPath() {

        if (SystemUtils.IS_OS_WINDOWS) {
            return "C:\\ProgramData\\sitoolkit\\repository";
        } else {
            return System.getProperty("user.home") + "/.sitoolkit/repository";
        }
    }

}
