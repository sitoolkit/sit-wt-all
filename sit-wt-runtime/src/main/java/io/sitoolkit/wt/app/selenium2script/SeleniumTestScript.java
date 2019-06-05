package io.sitoolkit.wt.app.selenium2script;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SeleniumTestScript {

  private List<SeleniumTestStep> testStepList = new ArrayList<>();

  private String baseUrl;

  private String name;

}
