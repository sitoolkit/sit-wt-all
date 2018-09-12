package io.sitoolkit.wt.util.app.proxysetting;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.sitoolkit.wt.util.domain.proxysetting.ProxySettingProcessClient;
import io.sitoolkit.wt.util.infra.maven.MavenUtils;
import io.sitoolkit.wt.util.infra.proxysetting.ProxySetting;

public class ProxySettingService {
    private static final Logger LOG = Logger.getLogger(ProxySettingService.class.getName());

    private static ProxySettingService proxySettingService = new ProxySettingService();

    private boolean loaded;

    private ProxySettingService() {
        loaded = false;
    }

    public static ProxySettingService getInstance() {
        return proxySettingService;
    }

    public void loadProxy() {

        if (loaded) return;

        try {
            ProxySetting proxySetting = MavenUtils.readProxySetting();

            if (proxySetting == null) {
                LOG.log(Level.INFO, "read registry proxy settings");
                ProxySettingProcessClient client = new ProxySettingProcessClient();
                proxySetting = client.getRegistryProxy();

                if (proxySetting.isEnabled()) {
                    if (!MavenUtils.writeProxySetting(proxySetting))
                        return;
                }
            }

            setProperties(proxySetting);
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "set proxy failed", exp);
        } finally {
            loaded = true;
        }
    }

    private void setProperties(ProxySetting proxySetting) {
        System.setProperty("proxySet", proxySetting.getProxyActive());

        if (proxySetting.isEnabled()) {
            LOG.log(Level.INFO, "set proxy properties");
            System.setProperty("proxyHost", proxySetting.getProxyHost());
            System.setProperty("proxyPort", proxySetting.getProxyPort());

            if (proxySetting.getNonProxyHosts() != null && !proxySetting.getNonProxyHosts().isEmpty()) {
                System.setProperty("nonProxyHosts", proxySetting.getNonProxyHosts());
            }
        } else {
            LOG.log(Level.INFO, "proxy settings is disabled");
        }
    }
}
