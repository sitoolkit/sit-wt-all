package org.sitoolkit.wt.infra;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SitPathUtilsTest {

    @Test
    public void testBuildUrlStartHttp() throws IOException {
        assertEquals("http://localhost:8080/input.html",
                SitPathUtils.buildUrl("http://localhost:8080/", "input.html"));
        assertEquals("http://localhost:8080/input.html",
                SitPathUtils.buildUrl("http://localhost:8080", "input.html"));
    }

    @Test
    public void testBuildUrlNull() throws IOException {
        String fileUrl = new File("src/main/webapp/input.html").toURI().toString();

        assertEquals(fileUrl, SitPathUtils.buildUrl(null, "input.html"));
    }

    @Test
    public void testBuildUrl() throws IOException {
        String fileUrl = new File("src/main/webapp/input.html").toURI().toString();

        assertEquals(fileUrl, SitPathUtils.buildUrl("src/main/webapp", "input.html"));
    }

}
