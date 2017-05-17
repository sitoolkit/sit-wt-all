package org.sitoolkit.wt.util.infra.process;

import java.util.ArrayList;
import java.util.List;

public class TestStdoutListener implements StdoutListener {

    private List<String> lines = new ArrayList<>();

    @Override
    public void nextLine(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }
}
