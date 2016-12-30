package org.sitoolkit.wt.gui.infra.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class VersionUtilsTest {

    @Test
    public void test() {
        assertThat("major", true, is(VersionUtils.isNewer("1.0", "2.0")));
        assertThat("minor", true, is(VersionUtils.isNewer("2.0", "2.1")));
        assertThat("fix", true, is(VersionUtils.isNewer("2.0", "2.0.1")));
        assertThat("fix", true, is(VersionUtils.isNewer("2.0.1", "2.0.2")));

        assertThat("major", false, is(VersionUtils.isNewer("2.0", "1.0")));
        assertThat("minor", false, is(VersionUtils.isNewer("2.1", "2.0")));
        assertThat("fix", false, is(VersionUtils.isNewer("2.0.1", "2.0")));
        assertThat("fix", false, is(VersionUtils.isNewer("2.0.2", "2.0.1")));
    }

}
