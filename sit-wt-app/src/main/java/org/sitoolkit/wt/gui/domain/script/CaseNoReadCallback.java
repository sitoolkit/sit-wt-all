package org.sitoolkit.wt.gui.domain.script;

import java.util.List;

@FunctionalInterface
public interface CaseNoReadCallback {

    void onRead(List<String> caseNos);
}
