package io.sitoolkit.wt.gui.domain.diffevidence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.sitoolkit.wt.app.compareevidence.BaseEvidenceManager;
import io.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator;
import io.sitoolkit.wt.app.compareevidence.MaskEvidenceGenerator;
import io.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class DiffEvidenceProcessClient {

    public void genMaskEvidence(File targetDir, ProcessParams params) {
        MaskEvidenceGenerator.staticExecute(targetDir.getPath());
        executeCallbacks(params.getExitClallbacks(), 0);
    }

    public void setBaseEvidence(File targetDir, ProcessParams params) {
        BaseEvidenceManager.staticExecute(targetDir.getPath());
        executeCallbacks(params.getExitClallbacks(), 0);
    }

    public void genDiffEvidence(File baseDir, File targetDir, ProcessParams params) {

        List<String> args = new ArrayList<>();
        if (baseDir != null) {
            args.add(baseDir.getPath());
        }
        if (targetDir != null) {
            args.add(targetDir.getPath());
        }
        DiffEvidenceGenerator.staticExecute(args.toArray(new String[0]));
        executeCallbacks(params.getExitClallbacks(), 0);
    }

    public void executeCallbacks(List<ProcessExitCallback> callbacks, int exitCode) {
        for (ProcessExitCallback callback : callbacks) {
            callback.callback(exitCode);
        }
    }
}
