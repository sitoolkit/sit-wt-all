package io.sitoolkit.wt.gui.infra.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class VersionUtils {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(VersionUtils.class);

    private VersionUtils() {
    }

    public static String get() {
        try {
            Enumeration<URL> resources = VersionUtils.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");

            while (resources.hasMoreElements()) {
                URL res = resources.nextElement();

                Manifest manifest = new Manifest(res.openStream());

                if ("sit-wt-app"
                        .equals(manifest.getMainAttributes().getValue("Implementation-Title"))) {
                    return manifest.getMainAttributes().getValue("Implementation-Version");
                }
            }

        } catch (IOException e) {
            LOG.warn("app.getVersionFailed", e);
        }
        return "";
    }

    public static boolean isNewer(String currentVer, String newVer) {
        if (StrUtils.isEmpty(currentVer) || StrUtils.isEmpty(newVer)) {
            return false;
        }
        return currentVer.compareTo(newVer) < 0;
    }

    public static void setSitWtVersion(File pomFile, String newVersion) {
        setSitWtVersion(pomFile, newVersion, pomFile);
    }

    public static int setSitWtVersion(File pomFile, String newVersion, File destPomFile) {

        if (StrUtils.isEmpty(newVersion)) {
            LOG.warn("app.versionEmpty");
            return 2;
        }

        try {
            Document document = parseSettingFile(pomFile);

            Node versionNode = (Node) XPathFactory.newInstance().newXPath()
                    .compile("/project/properties/sitwt.version")
                    .evaluate(document, XPathConstants.NODE);

            String currentVersion = versionNode.getTextContent();

            if (currentVersion.equals(newVersion)) {
                LOG.info("app.sameVersion", pomFile.getAbsolutePath(), currentVersion);
                return 1;
            }

            LOG.info("app.updateVersion", pomFile.getAbsolutePath(), currentVersion, newVersion);
            versionNode.setTextContent(newVersion);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(destPomFile);
            transformer.transform(domSource, result);

            return 0;
        } catch (Exception e) {
            LOG.error("app.updateVersionFailed", e);
            return -1;
        }
    }

    public static Document parseSettingFile(File settingFile) throws Exception {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(settingFile);

        } catch (Exception exp) {
            throw exp;
        }
    }
}
