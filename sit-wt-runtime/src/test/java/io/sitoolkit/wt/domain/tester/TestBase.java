package io.sitoolkit.wt.domain.tester;

import org.junit.BeforeClass;
import io.sitoolkit.wt.app.sample.SampleGenerator;

public abstract class TestBase extends SitTesterTestBase {

  @BeforeClass
  public static void initialize() {
    SampleGenerator.generate();
  }

}
