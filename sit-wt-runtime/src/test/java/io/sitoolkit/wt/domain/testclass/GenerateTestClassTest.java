package io.sitoolkit.wt.domain.testclass;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.junit.BeforeClass;
import org.junit.Test;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

public class GenerateTestClassTest {

  static TemplateEngine templateEngine;

  @BeforeClass
  public static void init() {
    Velocity.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, "./src/main/resources/");
    templateEngine = new TemplateEngineVelocityImpl();
  }

  @Test
  public void generate() throws IOException, URISyntaxException {
    TestClass testClass = new TestClass();
    testClass.setFileBase("GeneratedTestClass");
    testClass.setScriptPath("/path/to/script");
    testClass.getCaseNos().add("001");
    testClass.getCaseNos().add("00.2");

    Path expectedFile = Paths.get(getClass().getResource("/GeneratedTestClass.java").toURI());

    String expected = FileUtils.readFileToString(expectedFile.toFile(), StandardCharsets.UTF_8);
    String result = templateEngine.writeToString(testClass);

    assertEquals("出力内容", expected, result);
  }

}
