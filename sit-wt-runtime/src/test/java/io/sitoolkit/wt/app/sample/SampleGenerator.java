package io.sitoolkit.wt.app.sample;

public class SampleGenerator {

  private static boolean generated = false;

  public static synchronized void generate() {
    if (generated) {
      return;
    }

    generated = true;

    SampleManager.staticUnarchiveBasicSample();
  }

}
