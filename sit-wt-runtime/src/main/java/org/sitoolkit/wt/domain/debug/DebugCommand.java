package org.sitoolkit.wt.domain.debug;

import java.awt.Desktop;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.context.ApplicationContext;

class DebugCommand {
    private static SitLogger LOG = SitLoggerFactory.getLogger(DebugCommand.class);

    CommandKey key;
    String body;

    static Map<String, CommandKey> map = new HashMap<>();

    static {
        for (CommandKey key : CommandKey.values()) {
            if (key.key.length() > 0) {
                map.put(key.key.substring(0, 1), key);
            }
        }
    }

    static final DebugCommand NA = new DebugCommand(CommandKey.NA, "");

    public DebugCommand(CommandKey key, String body) {
        super();
        this.key = key;
        this.body = body;
    }

    static DebugCommand readLine(String line) {
        if (StringUtils.isBlank(line)) {
            return NA;
        }

        CommandKey key = map.get(line.substring(0, 1));
        if (key == null) {
            return NA;
        }

        return new DebugCommand(key, line.substring(1).trim());
    }

    /**
     * コマンド実行結果のテストステップインデックスを取得します。
     *
     * @param idx
     *            テストステップインデックス
     * @param testScript
     *            テストスクリプト
     * @return コマンド実行結果のテストステップインデックス
     */
    int execute(final int idx, TestScript testScript, ApplicationContext appCtx) {
        int ret = idx;
        switch (key) {
            case START:
                ret = idx + 1;
                break;
            case BACK:
                ret = idx - 1;
                break;
            case CURRENT:
                ret = idx;
                break;
            case FORWARD:
                ret = idx + 1;
                break;
            case EXEC_STEP_NO:
                ret = testScript.getIndexByScriptNo(body);
                break;
            case SET_STEP_NO:
                ret = testScript.getIndexByScriptNo(body) - 1;
                break;

            case LOC:
                LocatorChecker check = appCtx.getBean(LocatorChecker.class);

                Locator locator = Locator.build(body);

                if (locator.isNa()) {
                    LOG.info("format.valid");
                } else {
                    check.check(locator);
                }
                break;

            case SHOW_PARAM:
                showParam(appCtx);
                break;

            case INPUT_PARAM:
                inputParam(appCtx);
                break;

            case EXPORT:
                try {
                    TestScriptGenerateTool exporter = appCtx.getBean(TestScriptGenerateTool.class);
                    exporter.generateFromPage();
                } catch (Exception e) {
                    LOG.error("export.error", e);
                }
                break;

            case OPEN_SRCIPT:
                try {
                    Desktop.getDesktop().open(testScript.getScriptFile());
                } catch (IOException e) {
                    LOG.error("unexpected.error", e);
                }
                break;

            case EXIT:
                ret = testScript.getTestStepList().size();
                break;
            default:
                break;
        }

        return ret;
    }

    protected void inputParam(ApplicationContext appCtx) {
        TestContext testCtx = appCtx.getBean(TestContext.class);

        if (StringUtils.isBlank(body)) {
            LOG.info("format.valid");
            return;
        }

        body = body.trim();
        String key = StringUtils.substringBefore(body, " ");
        String value = StringUtils.substringAfter(body, " ");

        testCtx.addParam(key, value);
        LOG.info("add.param", key, value);
    }

    protected void showParam(ApplicationContext appCtx) {
        TestContext testCtx = appCtx.getBean(TestContext.class);

        if (testCtx.getParams().isEmpty()) {
            LOG.info("param.empty");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : testCtx.getParams().entrySet()) {
            sb.append("\n    ");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());

        }
        LOG.info("show.param", sb);
    }

}
