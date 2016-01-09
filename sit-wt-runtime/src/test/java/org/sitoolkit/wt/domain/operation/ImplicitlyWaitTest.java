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
package org.sitoolkit.wt.domain.operation;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author yuichi.kuwahara
 */
public class ImplicitlyWaitTest {

    static WebDriver driver;

    @BeforeClass
    public static void beforeClass() {
        driver = new FirefoxDriver();
    }

    @Before
    public void before() {
        File file = new File("src/main/webapp/wait.html");
        driver.get(file.toURI().toString());
    }

    @Test
    public void testInTime() {
        driver.manage().timeouts().implicitlyWait(3100, TimeUnit.MILLISECONDS);
        WebElement element = driver.findElement(By.linkText("Click!"));
        assertThat(element, not(nullValue()));
    }

    @Test
    public void testNotInTime() {
        driver.manage().timeouts().implicitlyWait(2700, TimeUnit.MILLISECONDS);
        try {
            driver.findElement(By.linkText("Click!"));
            fail();
        } catch (NoSuchElementException e) {
            // OK
        }
    }

    @AfterClass
    public static void after() {
        driver.close();
    }
}
