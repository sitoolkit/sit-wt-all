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

  private GuidanceUtils() {}

  /**
   * ガイダンスファイルをカレントディレクトリに展開します。
   *
   * @param resources ガイダンスの表示に必要なファイル
   */
  public static void retrieve(String[] resources) {
    String projectDir = System.getProperty("sitwt.projectDirectory");
    String destDir = (StringUtils.isEmpty(projectDir)) ? "." : projectDir;
    retrieve(resources, new File(destDir));
  }

  /**
   * ガイダンスファイルを指定のディレクトリに展開します。
   *
   * @param resources ガイダンスの表示に必要なファイル
   * @param destDir 展開先のディレクトリ
   */
  public static void retrieve(String[] resources, File destDir) {

    for (String res : resources) {
      try {
        URL resUrl = ResourceUtils.getURL("classpath:" + res);
        File destFile = new File(destDir, res);
        if (destFile.exists()) {
          continue;
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
    String projectDir = System.getProperty("sitwt.projectDirectory");
    String guidancePath =
        (StringUtils.isEmpty(projectDir)) ? guidanceFile : projectDir + "/" + guidanceFile;
    return new File(guidancePath).toURI() + (StringUtils.isEmpty(baseUrl) ? "" : "?" + baseUrl);
  }
}
