package io.sitoolkit.wt.util.infra.process;

import java.util.ArrayList;
import java.util.List;

import io.sitoolkit.wt.util.infra.process.StdoutListener;

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
