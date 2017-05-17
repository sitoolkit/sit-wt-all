package org.sitoolkit.wt.gui.domain.test;

import org.sitoolkit.wt.util.infra.process.StdoutListener;

public class MavenClasspahListener implements StdoutListener {

    private boolean start = false;

    private StringBuilder sb = new StringBuilder();

    public MavenClasspahListener() {
    }

    @Override
    public void nextLine(String line) {

        if (start) {
            if (line.startsWith("[INFO")) {
                start = false;
            } else {
                sb.append(line);
            }
        }

        if ("[INFO] Dependencies classpath:".equals(line)) {
            start = true;
        }

    }

    public String getClasspath() {
        return sb.toString();
    }

}
