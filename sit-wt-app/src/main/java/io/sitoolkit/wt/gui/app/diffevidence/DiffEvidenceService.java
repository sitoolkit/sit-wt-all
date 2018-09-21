package io.sitoolkit.wt.gui.app.diffevidence;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.sitoolkit.wt.gui.domain.diffevidence.DiffEvidenceProcessClient;
import io.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class DiffEvidenceService {

    private String evidenceDirRegex = "^evidence_.*";

    private Pattern evidenceDirPattern = Pattern.compile(evidenceDirRegex);

    DiffEvidenceProcessClient client = new DiffEvidenceProcessClient();

    public boolean genMaskEvidence(File selectedItem, ProcessExitCallback callback) {

        if (selectedItem == null) {
            return false;
        }

        Matcher m = evidenceDirPattern.matcher(selectedItem.getName());
        if (!m.matches()) {
            return false;
        }

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        client.genMaskEvidence(selectedItem, params);

        return true;
    }

    public boolean setBaseEvidence(File selectedItem, ProcessExitCallback callback) {

        if (selectedItem == null) {
            return false;
        }

        Matcher m = evidenceDirPattern.matcher(selectedItem.getName());
        if (!m.matches()) {
            return false;
        }

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        client.setBaseEvidence(selectedItem, params);

        return true;
    }

    public boolean genDiffEvidence(File projectDir, List<File> selectedFiles,
            ProcessExitCallback callback) {

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

        targetDir = projectDir.toPath().relativize(targetDir.toPath()).toFile();

        client.genDiffEvidence(baseDir, targetDir, params);

        return true;
    }

}
