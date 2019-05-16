package io.sitoolkit.wt.infra.selenium;

import java.util.Arrays;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebDriverUtils {

  private WebDriverUtils() {}

  public static Object executeScript(WebDriver driver, String script, Object... args) {

    JavascriptExecutor executor = (JavascriptExecutor) driver;

    JavascriptException exeption = null;

    for (int i = 0; i < 3; i++) {
      try {
        return executor.executeScript(script, args);
      } catch (JavascriptException e) {
        log.debug("Javascript execution failed script:{}, args:{}", script, Arrays.toString(args));
        log.debug("Error detail", e);
        exeption = e;
      }
    }

    throw exeption;
  }

}
