package io.sitoolkit.wt.util.domain.proxysetting;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import io.sitoolkit.wt.util.domain.proxysetting.ProxySettingStdoutListener;
import io.sitoolkit.wt.util.infra.proxysetting.ProxySetting;

public class ProxySettingStdoutListenerTest {

    @Test
    public void test001() {
        String[] messages = {
                "ProxyEnable    REG_DWORD    0x1",
                "ProxyServer    REG_SZ    127.0.0.1:8080",
                "ProxyOverride    REG_SZ    192.168.0.*;127.0.0.1"
        };

        ProxySettingStdoutListener listener = new ProxySettingStdoutListener();
        for (String message : messages) {
            listener.parse(message);
        }

        ProxySetting proxySetting = listener.getProxySetting();
        assertThat(proxySetting.getProxyActive(), is("true"));
        assertThat(proxySetting.getProxyHost(), is("127.0.0.1"));
        assertThat(proxySetting.getProxyPort(), is("8080"));
        assertThat(proxySetting.getNonProxyHosts(), is("192.168.0.*|127.0.0.1"));
    }

    @Test
    public void test002() {
        String[] messages = {
                "ProxyEnable    REG_DWORD    0x1",
                "ProxyServer    REG_SZ    127.0.0.1"
        };

        ProxySettingStdoutListener listener = new ProxySettingStdoutListener();
        for (String message : messages) {
            listener.parse(message);
        }

        ProxySetting proxySetting = listener.getProxySetting();
        assertThat(proxySetting.getProxyActive(), is("true"));
        assertThat(proxySetting.getProxyHost(), is("127.0.0.1"));
        assertThat(proxySetting.getProxyPort(), is("80"));
        assertThat(proxySetting.getNonProxyHosts(), is(""));
    }

    @Test
    public void test003() {
        String[] messages = {
                "ProxyEnable    REG_DWORD    0x1",
                "ProxyServer    REG_SZ    http=127.0.0.1:8080;https=127.0.0.2:8081;ftp=127.0.0.3:8082;socks=127.0.0.4:8083"
        };

        ProxySettingStdoutListener listener = new ProxySettingStdoutListener();
        for (String message : messages) {
            listener.parse(message);
        }

        ProxySetting proxySetting = listener.getProxySetting();
        assertThat(proxySetting.getProxyActive(), is("true"));
        assertThat(proxySetting.getProxyHost(), is("127.0.0.1"));
        assertThat(proxySetting.getProxyPort(), is("8080"));
        assertThat(proxySetting.getNonProxyHosts(), is(""));
    }
}
