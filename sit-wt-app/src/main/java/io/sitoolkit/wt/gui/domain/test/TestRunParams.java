package io.sitoolkit.wt.gui.domain.test;

import java.io.File;

import io.sitoolkit.wt.domain.debug.DebugListener;
import lombok.Data;

@Data
public class TestRunParams {

    private File baseDir;

    private File projectDir;

    private String baseUrl;

    private boolean debug;

    private boolean parallel;

    private boolean compareScreenshot;

    private String driverType;

    private String targetScripts;

    private DebugListener debugListener;
}
