package org.sitoolkit.wt.domain.evidence;

public enum MessagePattern {
    項目をXXします("{}({})を{}します"), 項目にXXをYYします("{}({})に{}を{}します"), 項目をXXします_URL_エビデンス(
            "{}({})を{}します ({}) <a href=\"{}\" target=\"evidence\">エビデンスを表示</a>"), 項目にXXをYYします_URL_エビデンス(
                    "{}({})に{}を{}します ({}) <a href=\"{}\" target=\"evidence\">エビデンスを表示</a>");

    private String pattern;

    private MessagePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
