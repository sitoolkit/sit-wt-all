package org.sitoolkit.wt.util.infra.proxysetting;

import java.util.HashMap;

import org.sitoolkit.wt.util.infra.util.StrUtils;

public class ProxySetting {
    private String proxyActive = "false";

    private String proxyHost = "";

    private String proxyPort = "";

    private String nonProxyHosts = "";

    public void setRegistryResult(HashMap<String, String> proxy) {
        setProxySettings(
                proxy.get("host"),
                proxy.get("port"),
                proxy.get("nonProxyHosts")
                );
    }

    public void setProxySettings(String host, String port, String nonProxyHosts) {
        this.proxyActive = "true";
        this.proxyHost = host;
        this.proxyPort = port;
        this.nonProxyHosts = nonProxyHosts;
    }

    public String getProxyActive() {
        return proxyActive;
    }

    public void setProxyActive(String proxyActive) {
        this.proxyActive = proxyActive;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getNonProxyHosts() {
        return nonProxyHosts;
    }

    public void setNonProxyHosts(String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }

    public boolean isEnabled() {
        return ("true".equals(getProxyActive()) && !StrUtils.isEmpty(getProxyHost()));
    }
}
