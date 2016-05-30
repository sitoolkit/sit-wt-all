/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.operation.selenium;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 *
 * @author yuichi.kuwahara
 */
public class OpenOperationTest {

    OpenOperation ope = new OpenOperation();

    @Test
    public void testBuildUrlStartHttp() throws IOException {
        assertEquals("http://localhost:8080/input.html",
                ope.buildUrl("http://localhost:8080/", "input.html"));
        assertEquals("http://localhost:8080/input.html",
                ope.buildUrl("http://localhost:8080", "input.html"));
    }

    @Test
    public void testBuildUrlNull() throws IOException {
        String fileUrl = new File("src/main/webapp/input.html").toURI().toString();

        assertEquals(fileUrl, ope.buildUrl(null, "input.html"));
    }

    @Test
    public void testBuildUrl() throws IOException {
        String fileUrl = new File("src/main/webapp/input.html").toURI().toString();

        assertEquals(fileUrl, ope.buildUrl("src/main/webapp", "input.html"));
    }

}