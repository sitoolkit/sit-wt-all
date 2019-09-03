package io.github.bonigarcia.wdm;

import io.sitoolkit.wt.app.config.RuntimeConfig;
import java.awt.Desktop;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverInfo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class WebDriverManagerTest {

  @Test
  public void chromeDriverTest() {
//    WebDriverManager.chromedriver().setup();
//    WebDriver driver = new ChromeDriver();
  }

  @Test
  public void ieDriverTest() {
//    WebDriverManager.iedriver().setup();
//    WebDriver driver = new InternetExplorerDriver();
//    driver.close();
  }

  @Test
  public void edgeDriverTest() {
//    WebDriverManager.edgedriver().setup();
//    WebDriver driver = new EdgeDriver();
//    driver.close();
  }

  @Test
  public void safariDriverTest() {
//    SafariDriver driver = new SafariDriver();
//    driver.close();
  }

}
