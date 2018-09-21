package io.sitoolkit.wt.infra;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import io.sitoolkit.wt.infra.PropertyManager;

public class PropertyManagerTest {

    PropertyManager pm = new PropertyManager();

    @Test
    public void test() {

        assertDriverFlags("firefox", "", true, false, false, false);
        assertDriverFlags("remote", "firefox", true, false, false, false);
        assertDriverFlags("ie", "", false, true, false, true);
        assertDriverFlags("internet explorer", "", false, true, false, true);
        assertDriverFlags("remote", "internet explorer", false, true, false, true);
        assertDriverFlags("edge", "", false, false, true, true);
        assertDriverFlags("remote", "edge", false, false, true, true);

    }

    private void assertDriverFlags(String driverType, String browserName, boolean isFf,
            boolean isIe, boolean idEdge, boolean isMs) {
        pm.setDriverFlags(driverType, browserName);
        String s = "driverType:" + driverType + ", browserName:" + browserName;
        assertThat(s + " is firefox", pm.isFirefoxDriver(), is(isFf));
        assertThat(s + " is ie", pm.isIeDriver(), is(isIe));
        assertThat(s + " is edge", pm.isEdgeDriver(), is(idEdge));
        assertThat(s + " is ms", pm.isMsDriver(), is(isMs));
    }
}
