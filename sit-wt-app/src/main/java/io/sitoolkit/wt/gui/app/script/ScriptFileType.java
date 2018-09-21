package io.sitoolkit.wt.gui.app.script;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public enum ScriptFileType {

        CSV_UTF8(
                "test script (csv/UTF-8)",
                true,
                StandardCharsets.UTF_8,
                true,
                "*.csv"),

        CSV_UTF8_NO_BOM(
                "test script (csv/UTF-8(no-bom))",
                true,
                StandardCharsets.UTF_8,
                false,
                "*.csv"),

        CSV_SJIS(
                "test script (csv/Shift-JIS)",
                true,
                Charset.forName("Windows-31J"),
                false ,
                "*.csv"),

        EXCEL(
                "test script (excel)",
                "*.xlsx"),
        ;

        @Getter
        private final String description;
        @Getter
        private final boolean isTextFile;
        @Getter
        private final Charset charset;
        @Getter
        private final boolean hasBom;
        @Getter
        private final List<String> extentions;

        private ScriptFileType(String description, String... extentions) {
            this(description, false, null, false, extentions);
        }

        private ScriptFileType(String description, boolean isBinaryFile, Charset charset ,boolean hasBom, String... extentions) {
            this.description = description;
            this.isTextFile = isBinaryFile;
            this.charset = charset;
            this.hasBom = hasBom;
            this.extentions = Arrays.asList(extentions);
        }
}
