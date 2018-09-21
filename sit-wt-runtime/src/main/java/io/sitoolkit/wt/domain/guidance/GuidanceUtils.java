package io.sitoolkit.wt.domain.guidance;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class GuidanceUtils {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(GuidanceUtils.class);

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
                LOG.info("guidance.file.open", destFile.getAbsolutePath());
                FileUtils.copyURLToFile(resUrl, destFile);
            } catch (IOException e) {
                LOG.warn("guidance.file.open.error", e);
            } catch (Exception exp) {
                LOG.warn("proxy.error", exp);
            }
        }

    }

    public static String appendBaseUrl(String guidanceFile, String baseUrl) {
        return new File(guidanceFile).toURI() + (StringUtils.isEmpty(baseUrl) ? "" : "?" + baseUrl);
    }
}
