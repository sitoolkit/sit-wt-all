package io.sitoolkit.wt.domain.testscript;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.wt.infra.resource.MessageManager;
import lombok.Getter;

public enum ScreenshotTiming {
    NONE(""), BEFORE, AFTER, AROUND;

    /**
     * key: label
     */
    private static Map<String, ScreenshotTiming> timingMap = new LinkedHashMap<>();

    @Getter
    private String label;

    static {
        for (ScreenshotTiming timing : values()) {
            timingMap.put(timing.getLabel(), timing);
        }
    }

    private ScreenshotTiming(String label) {
        this.label = label;
    }

    private ScreenshotTiming() {
        this.label = MessageManager.getMessage("testScript-screenshot-" + name().toLowerCase());
    }

    public static ScreenshotTiming getTiming(String value) {
        return timingMap.get(StringUtils.defaultIfBlank(value, NONE.getLabel()));
    }

    public static List<String> getLabels() {
        return new ArrayList<>(timingMap.keySet());
    }

}
