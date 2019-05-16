package io.sitoolkit.wt.infra.csv;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.io.ByteOrderMark;
import com.opencsv.CSVWriter;
import io.sitoolkit.wt.infra.PropertyManager;

public class CsvFileWriter {

  @Resource
  PropertyManager pm;

  public void write(List<List<String>> data, String absolutePath) {

    List<String[]> allLines = data.stream().map(list -> list.toArray(new String[list.size()]))
        .collect(Collectors.toList());

    writeFile(allLines, absolutePath);

  }

  private void writeFile(List<String[]> allLines, String absolutePath) {

    try (OutputStream out = new FileOutputStream(absolutePath);
        Writer writer = new OutputStreamWriter(out, pm.getCsvCharset());
        CSVWriter csvWriter = new CSVWriter(writer)) {

      if (pm.isCsvHasBOM() && StandardCharsets.UTF_8.equals(pm.getCsvCharset())) {
        out.write(ByteOrderMark.UTF_8.getBytes());
      }

      boolean applyQuotesToAll = false;
      csvWriter.writeAll(allLines, applyQuotesToAll);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
