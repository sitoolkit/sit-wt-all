package io.sitoolkit.wt.domain.debug;

import org.apache.commons.lang3.StringUtils;

enum CommandKey {
    START       ("s", true,  "テスト実行を再開します。"),
    BACK        ("b", false, "テストステップを1つ戻します。"),
    CURRENT     ("c", true,  "現在のテストステップを実行します。"),
    FORWARD     ("f", true,  "次のテストステップを実行します。"),
    EXEC_STEP_NO("!", true,  "! [0]と入力するとNo.[0]のテストステップを実行します。"),
    SET_STEP_NO ("#", false, "# [0]と入力するとNo.[0]のテストステップから開始するように設定します。"),
    EXPORT      ("e", false,  "テストスクリプトを作成します。"),
    LOC         ("l", false, "l [0] [1]と入力するとロケーター書式[0]、値[1]に該当する要素を検出します。"),
    SHOW_PARAM  ("p", false, "ストアされているパラメーターを表示します。"),
    INPUT_PARAM ("i", false, "i [0] [1]と入力すると、名前[0]、値[1]のパラメーターをストアします。"),
    OPEN_SRCIPT ("o", false, "現在実行中のテストスクリプトを開きます。"),
    EXIT        ("q", true,  "テスト実行を終了します。"),
    NA          ("",  false, "")
    ;

    String key;

    String description;

    /**
     * 当該キーが一時停止を解除するものである場合にtrueを返します。
     */
    boolean release;

    private CommandKey(String key, boolean release, String description) {
        this.key = key;
        this.release = release;
        this.description = description;
    }

    static String buildUsage() {
        StringBuilder sb = new StringBuilder();

        sb.append("デバッグ操作方法：\n");
        for (CommandKey cmd : CommandKey.values()) {
            if (cmd.key.isEmpty()) {
                continue;
            }
            sb.append(StringUtils.rightPad(cmd.key, 6));
            sb.append(": ");
            sb.append(cmd.description);
            sb.append("\n");
        }

        return sb.toString();
    }


}
