package org.sitoolkit.wt.gui.app.diffevidence;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sitoolkit.wt.gui.domain.diffevidence.DiffEvidenceProcessClient;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class DiffEvidenceService {

    private String evidenceDirRegex = "^evidence_.*";

    private Pattern evidenceDirPattern = Pattern.compile(evidenceDirRegex);

    DiffEvidenceProcessClient client = new DiffEvidenceProcessClient();

    public boolean genMaskEvidence(List<File> selectedFiles, ProcessExitCallback callback) {

        if (selectedFiles.size() != 1) {
            return false;
        }

        Matcher m = evidenceDirPattern.matcher(selectedFiles.get(0).getName());
        if (!m.matches()) {
            return false;
        }

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        client.genMaskEvidence(selectedFiles.get(0), params);

        return true;
    }

    public boolean setBaseEvidence(List<File> selectedFiles, ProcessExitCallback callback) {

        if (selectedFiles.size() != 1) {
            return false;
        }

        Matcher m = evidenceDirPattern.matcher(selectedFiles.get(0).getName());
        if (!m.matches()) {
            return false;
        }

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        client.setBaseEvidence(selectedFiles.get(0), params);

        return true;
    }

    public boolean genDiffEvidence(List<File> selectedFiles, ProcessExitCallback callback) {

        int selectedCount = selectedFiles.size();

        if (selectedCount > 2) {
            return false;
        }

        for (File file : selectedFiles) {
            Matcher m = evidenceDirPattern.matcher(file.getName());
            if (!m.matches()) {
                return false;
            }
        }

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        File baseDir = null;
        File targetDir = null;
        if (selectedCount == 2) {
            baseDir = selectedFiles.get(0);
            targetDir = selectedFiles.get(1);
        } else if (selectedCount == 1) {
            targetDir = selectedFiles.get(0);
        }

        client.genDiffEvidence(baseDir, targetDir, params);

        return true;
    }

}
