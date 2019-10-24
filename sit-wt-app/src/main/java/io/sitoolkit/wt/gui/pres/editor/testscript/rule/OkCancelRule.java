package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OkCancelRule implements ChooseableRule {

  private static final String BLANK = "";
  private static final String OK = "ok";
  private static final String CANCEL = "cancel";

  private static final Pattern OK_PATTERN =
      Pattern.compile("(ok|true|y)", Pattern.CASE_INSENSITIVE);

  private static List<String> items = Arrays.asList(BLANK, OK, CANCEL);

  @Getter public static OkCancelRule instance = new OkCancelRule();

  @Override
  public boolean match(String value) {
    return true;
  }

  @Override
  public boolean isChangeable() {
    return true;
  }

  @Override
  public String defalutValue() {
    return BLANK;
  }

  @Override
  public List<String> getChoices() {
    return items;
  }

  // TODO call convertValue
  public String convertValue(Object value) {
    if (StringUtils.isBlank((String) value)) {
      return BLANK;
    }

    Matcher matcher = OK_PATTERN.matcher(value.toString());
    return matcher.find() ? OK : CANCEL;
  }
}
