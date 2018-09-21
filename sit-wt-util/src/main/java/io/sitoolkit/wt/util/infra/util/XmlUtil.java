package io.sitoolkit.wt.util.infra.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlUtil {
    private static final Logger LOG = Logger.getLogger(XmlUtil.class.getName());

    public static void writeXml(Document document, File file){

        Transformer transformer = null;
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("encoding", "UTF-8");

            transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "write settings.xml failed", exp);
        }
    }

    public static Element getChildElement(Element element, String target) {
        NodeList nodes = element.getElementsByTagName("*");

        Element child = null;
        for (int cnt = 0 ; cnt < nodes.getLength() ; cnt++) {
            if (target.equals(nodes.item(cnt).getNodeName())) {
                child = (Element) nodes.item(cnt);
                break;
            }
        }

        return child;
    }
}
