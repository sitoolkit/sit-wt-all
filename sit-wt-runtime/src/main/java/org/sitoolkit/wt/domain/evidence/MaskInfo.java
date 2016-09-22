package org.sitoolkit.wt.domain.evidence;

import java.util.ArrayList;
import java.util.List;

public class MaskInfo {

    private List<ElementPosition> positions = new ArrayList<>();

    private MaskInfo() {

    }

    public static MaskInfo load(EvidenceDir baseDir) {
        MaskInfo maskInfo = new MaskInfo();

        // TODO 実装

        return maskInfo;
    }

    public List<ElementPosition> getPositions() {
        return positions;
    }

}
