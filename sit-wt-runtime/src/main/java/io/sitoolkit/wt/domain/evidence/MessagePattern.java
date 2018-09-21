package io.sitoolkit.wt.domain.evidence;

public enum MessagePattern {
    項目をXXします("pattern.xx"), 項目にXXをYYします("pattern.xx.yy"), 項目をXXします_URL_エビデンス(
            "pattern.xx.url"), 項目にXXをYYします_URL_エビデンス("pattern.xx.yy.url");

    private String pattern;

    private MessagePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
