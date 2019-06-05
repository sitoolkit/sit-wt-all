package io.sitoolkit.wt.infra;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonUtils() {}

  public static JsonNode readTree(Path path) {
    try {
      return MAPPER.readTree(path.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
