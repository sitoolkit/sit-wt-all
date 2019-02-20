package io.sitoolkit.wt.domain.operation;

import java.util.List;

public interface OperationConverter {

    Operation convert(String name);

    List<String> getOperationNames();

}
