package io.sitoolkit.wt.util.infra.process;

import java.util.ArrayList;
import java.util.List;

public class ConversationProcessContainer {

  private static List<ConversationProcess> processes = new ArrayList<>();

  private ConversationProcessContainer() {}

  public static ConversationProcess create() {
    ConversationProcess process = new ConversationProcess();
    processes.add(process);
    return process;
  }

  public static void destroy() {

    for (ConversationProcess process : processes) {
      process.destroy();
    }

  }
}
