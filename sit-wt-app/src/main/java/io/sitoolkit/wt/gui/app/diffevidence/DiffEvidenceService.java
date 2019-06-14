package io.sitoolkit.wt.gui.app.diffevidence;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import io.sitoolkit.wt.app.compareevidence.BaseEvidenceManager;
import io.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator;
import io.sitoolkit.wt.app.compareevidence.MaskEvidenceGenerator;
import io.sitoolkit.wt.app.compareevidence.MaskScreenshotGenerator;
import io.sitoolkit.wt.domain.evidence.EvidenceDir;
import io.sitoolkit.wt.domain.evidence.EvidenceOpener;

public class DiffEvidenceService {

  private String evidenceDirRegex = "^evidence_.*";

  private Pattern evidenceDirPattern = Pattern.compile(evidenceDirRegex);

  MaskEvidenceGenerator maskEvidenceGenerator = new MaskEvidenceGenerator();

  MaskScreenshotGenerator maskScreenShotGenerator = new MaskScreenshotGenerator();

  EvidenceOpener evidenceOpener = new EvidenceOpener();

  BaseEvidenceManager baseEvidenceManager = new BaseEvidenceManager();

  @Resource
  DiffEvidenceGenerator DiffEvidenceGenerator;

  public boolean genMaskEvidence(File selectedItem) {

    if (selectedItem == null) {
      return false;
    }

    Matcher m = evidenceDirPattern.matcher(selectedItem.getName());
    if (!m.matches()) {
      return false;
    }

    EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(selectedItem.getAbsolutePath());
    maskScreenShotGenerator.generate(targetDir);
    maskEvidenceGenerator.generate(targetDir);
    evidenceOpener.openMaskEvidence(targetDir);

    return true;
  }

  public boolean setBaseEvidence(File selectedItem) {

    if (selectedItem == null) {
      return false;
    }

    Matcher m = evidenceDirPattern.matcher(selectedItem.getName());
    if (!m.matches()) {
      return false;
    }

    EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(selectedItem.getAbsolutePath());
    baseEvidenceManager.setBaseEvidence(targetDir);

    return true;
  }

  public boolean genDiffEvidence(File projectDir, List<File> selectedFiles) {

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

    String baseDir = null;
    String targetDir = null;
    if (selectedCount == 2) {
      baseDir = selectedFiles.get(0).getAbsolutePath();
      targetDir = selectedFiles.get(1).getAbsolutePath();
    } else if (selectedCount == 1) {
      targetDir = selectedFiles.get(0).getAbsolutePath();
    }

    EvidenceDir targetEvidence = EvidenceDir.targetEvidenceDir(targetDir);
    EvidenceDir baseEvidence = EvidenceDir.baseEvidenceDir(baseDir, targetEvidence.getBrowser());

    DiffEvidenceGenerator.generate(baseEvidence, targetEvidence, false);
    evidenceOpener.openCompareEvidence(targetEvidence);

    return true;
  }
}
