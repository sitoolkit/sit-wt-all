package org.sitoolkit.wt.domain.guidance;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class GuidanceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(GuidanceUtils.class);

    private GuidanceUtils() {
    }

    /**
     * ガイダンスファイルをカレントディレクトリに展開します。
     *
     * @param resources
     *            ガイダンスの表示に必要なファイル
     */
    public static void retrieve(String[] resources) {
        retrieve(resources, new File("."));
    }

    /**
     * ガイダンスファイルを指定のディレクトリに展開します。
     *
     * @param resources
     *            ガイダンスの表示に必要なファイル
     * @param destDir
     *            展開先のディレクトリ
     */
    public static void retrieve(String[] resources, File destDir) {

        for (String res : resources) {
            try {
                URL resUrl = ResourceUtils.getURL("classpath:" + res);
                File destFile = new File(destDir, res);
                if (destFile.exists()) {
                    return;
                }
                LOG.info("ガイダンスファイルを展開します {}", destFile.getAbsolutePath());
                FileUtils.copyURLToFile(resUrl, destFile);
            } catch (IOException e) {
                LOG.warn("ガイダンスファイルの展開で例外が発生しました", e);
            } catch (Exception exp) {
                LOG.warn("プロキシの取得で例外が発生しました", exp);
            }
        }

    }

    public static String appendBaseUrl(String guidanceFile, String baseUrl) {
        return new File(guidanceFile).toURI() + (StringUtils.isEmpty(baseUrl) ? "" : "?" + baseUrl);
    }
}
