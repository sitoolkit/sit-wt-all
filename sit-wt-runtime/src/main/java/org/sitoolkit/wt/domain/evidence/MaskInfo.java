package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaskInfo {

    private static final Logger LOG = LoggerFactory.getLogger(MaskInfo.class);

    private static String USER_HOME = System.getProperty("user.home");

    private static String DOWNLOAD_FOLDER = "Downloads";

    private static String baseEvidence = System.getProperty("evidence.base");

    private Map<String, List<ElementPosition>> maskInfoAsMap = new HashMap<>();

    private MaskInfo() {

    }

    public static MaskInfo load(EvidenceDir targetDir) {
        MaskInfo maskInfo = new MaskInfo();
        Map<String, List<ElementPosition>> evdenceMaskInfo = new HashMap<>();

        EvidenceDir baseDir = baseEvidence == null ? EvidenceDir.getBase(targetDir.getBrowser())
                : EvidenceDir.getInstance(baseEvidence);

        for (File evidenceFile : targetDir.getEvidenceFiles()) {

            String maskFileName = evidenceFile.getName() + ".json";

            String maskFilePathInBase = FilenameUtils.concat(baseDir.getDir().getPath(), "mask");
            File maskFileInBase = new File(maskFilePathInBase, maskFileName);

            String dlPath = StringUtils.join(new String[] { USER_HOME, DOWNLOAD_FOLDER }, "/");
            File maskFileInDl = new File(dlPath, maskFileName);

            String jsonString = null;
            try {

                if (maskFileInBase.exists()) {
                    jsonString = FileUtils.readFileToString(maskFileInBase, "UTF-8");
                } else if (maskFileInDl.exists()) {
                    jsonString = FileUtils.readFileToString(maskFileInDl, "UTF-8");
                    FileUtils.moveFileToDirectory(maskFileInDl, new File(maskFilePathInBase), true);
                } else {
                    continue;
                }

                convertToMap(evdenceMaskInfo, jsonString);

            } catch (IOException e) {
                LOG.info("マスクファイル読み込み時に予期せぬエラーが発生しました", e);
            }

        }

        maskInfo.setMaskInfoAsMap(evdenceMaskInfo);

        return maskInfo;
    }

    private static void convertToMap(Map<String, List<ElementPosition>> evidenceMaskInfo,
            String jsonString) {

        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObj = reader.readObject();
        reader.close();

        JsonArray jsonArray = jsonObj.getJsonArray("maskInfo");

        for (int i = 0; i < jsonArray.size(); i++) {

            JsonObject obj = jsonArray.getJsonObject(i);
            JsonArray posStyles = obj.getJsonArray("posStyle");

            if (posStyles.isEmpty()) {
                continue;
            }

            String imgSrc = FilenameUtils.getName(obj.getString("imgSrc"));
            List<ElementPosition> positions = new ArrayList<>();

            for (int j = 0; j < posStyles.size(); j++) {
                JsonObject obj2 = posStyles.getJsonObject(j);
                double posX = (double) obj2.getInt("x");
                double posY = (double) obj2.getInt("y");
                double width = (double) obj2.getInt("width");
                double height = (double) obj2.getInt("height");
                ElementPosition pos = new ElementPosition(posX, posY, width, height);
                positions.add(pos);
            }

            evidenceMaskInfo.put(imgSrc, positions);

        }

    }

    public Map<String, List<ElementPosition>> getMaskInfoAsMap() {
        return maskInfoAsMap;
    }

    public void setMaskInfoAsMap(Map<String, List<ElementPosition>> maskInfoAsMap) {
        this.maskInfoAsMap = maskInfoAsMap;
    }

}
