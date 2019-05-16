package io.sitoolkit.wt.domain.testscript;

import java.util.Arrays;
import java.util.List;

public enum DataType {
  value, label, execution, ok_cancel, verification_value, store_value, variable_name, window_size, key_operation, input_value, coordinates, na;

  public static List<DataType> SELECT_TYPES = Arrays.asList(label, value);

}
