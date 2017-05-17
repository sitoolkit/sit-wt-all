package org.sitoolkit.wt.gui.domain.script;

import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.util.infra.process.StdoutListener;

public class CaseNoStdoutListener implements StdoutListener {

    List<String> caseNoList = new ArrayList<>();

    @Override
    public void nextLine(String line) {

        if (line.startsWith("Case No:")) {
            caseNoList.add(line.split(":")[1]);
        }
    }

    public List<String> getCaseNoList() {
        return caseNoList;
    }
}
