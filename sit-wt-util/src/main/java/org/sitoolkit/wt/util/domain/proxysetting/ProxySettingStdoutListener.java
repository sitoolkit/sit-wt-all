package org.sitoolkit.wt.util.domain.proxysetting;

import org.sitoolkit.wt.util.infra.process.StdoutListener;
import org.sitoolkit.wt.util.infra.proxysetting.ProxySetting;

public class ProxySettingStdoutListener implements StdoutListener {

    private ProxySetting proxySetting = new ProxySetting();

    @Override
    public void nextLine(String line) {
        if (line.trim().startsWith("Proxy")) {
            parse(line.trim());
        }
    }

    public ProxySetting getProxySetting() {
        return proxySetting;
    }

    void parse(String line) {
        String[] details = line.split(" +");
        switch(details[0]) {
        case "ProxyEnable" :
            if ("0x1".equals(details[2])) {
                proxySetting.setProxyActive("true");
            }
            break;

        case "ProxyServer" :
            if (details[2].contains(";")) {
                for (String protocolDetail : details[2].split(";")) {
                    String[] protocolDetails = protocolDetail.split("[=:]");
                    String protocol = protocolDetails[0];

                    if ("http".equals(protocol) || "https".equals(protocol)) {
                        proxySetting.setProxyHost(protocolDetails[1]);
                        if (protocolDetails.length == 3) {
                            proxySetting.setProxyPort(protocolDetails[2]);
                        } else {
                            proxySetting.setProxyPort("80");
                        }
                        break;
                    }
                }
            } else {
                String[] settings = details[2].split(":");
                proxySetting.setProxyHost(settings[0]);
                if (settings.length == 2) {
                    proxySetting.setProxyPort(settings[1]);
                } else {
                    proxySetting.setProxyPort("80");
                }
            }
            break;

        case "ProxyOverride" :
            proxySetting.setNonProxyHosts(details[2].replaceAll(";", "|"));
            break;
        }
    }
}
