package io.sitoolkit.wt.domain.httpserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateModel;
import lombok.Setter;

public class SitHttpHandler implements HttpHandler {
  private static final SitLogger LOG = SitLoggerFactory.getLogger(SitHttpHandler.class);

  @Resource
  private TemplateEngine templateEngine;
  @Setter
  private String baseDir;

  public void handle(HttpExchange httpExchange) throws IOException {
    try {
      String requestUrl = StringUtils.substringBefore(httpExchange.getRequestURI().toString(), "?");
      Path requestPath = Paths.get(baseDir, requestUrl);
      setContentType(httpExchange, requestUrl);
      httpExchange.getResponseHeaders().add("connection", "close");

      byte[] response = "POST".equals(httpExchange.getRequestMethod())
          ? createResponseMessage(requestPath, readPostParams(httpExchange))
          : createResponseMessage(requestPath);

      httpExchange.sendResponseHeaders(200, response.length);
      httpExchange.getResponseBody().write(response);

    } catch (FileNotFoundException fe) {
      LOG.warn("httpserver.filenotfound", fe);
      httpExchange.getResponseHeaders().add("connection", "close");
      httpExchange.sendResponseHeaders(404, 0);

    } catch (Exception e) {
      LOG.warn("httpserver.internalerror", e);
      httpExchange.getResponseHeaders().add("connection", "close");
      httpExchange.sendResponseHeaders(503, 0);

    } finally {
      httpExchange.getResponseBody().close();
    }
  }

  private void setContentType(HttpExchange httpExchange, String requestUrl) {
    Headers header = httpExchange.getResponseHeaders();
    switch (StringUtils.substringAfterLast(requestUrl, ".")) {
      case "pdf":
        header.add("content-type", "application/pdf");
        break;

      case "css":
        header.add("content-type", "text/css");
        break;

      default:
        header.add("content-type", "text/html; charset=utf-8");
        break;
    }
  }

  private byte[] createResponseMessage(Path requestPath) throws IOException {
    return createResponseMessage(requestPath, null);
  }

  private byte[] createResponseMessage(Path requestPath, Map<String, String> postParams)
      throws IOException {
    if (Files.exists(requestPath)) {
      return FileUtils.readFileToByteArray(requestPath.toFile());

    } else if (Files.exists(Paths.get(StringUtils.replace(requestPath.toString(), "html", "vm")))) {
      return readVelocityTemplate(
          StringUtils.replace(requestPath.getFileName().toString(), "html", "vm"), postParams);

    } else if ("favicon.ico".equals(requestPath.getFileName().toString())) {
      return new byte[0];

    } else {
      throw new FileNotFoundException(requestPath.toString() + " is not exists");
    }
  }

  private byte[] readVelocityTemplate(String templatePath, Map<String, String> postParams)
      throws UnsupportedEncodingException {
    TemplateModel model = new TemplateModel();
    model.setTemplate("/webapp/" + templatePath);

    Map<String, String> vmProperties = new HashMap<>(MessageManager.getResourceAsMap());
    if (Objects.nonNull(postParams)) {
      postParams.forEach((k, v) -> vmProperties.put(k, v));
    }

    model.setProperties(vmProperties);
    return templateEngine.writeToString(model).getBytes("UTF-8");
  }

  private Map<String, String> readPostParams(HttpExchange httpExchange) throws FileUploadException {
    ServletFileUpload up = new ServletFileUpload(new DiskFileItemFactory());
    List<FileItem> result = up.parseRequest(new RequestContext() {
      @Override
      public String getCharacterEncoding() {
        return "UTF-8";
      }

      @Override
      public int getContentLength() {
        return 0;
      }

      @Override
      public String getContentType() {
        return httpExchange.getRequestHeaders().getFirst("Content-type");
      }

      @Override
      public InputStream getInputStream() throws IOException {
        return httpExchange.getRequestBody();
      }
    });

    return result.stream().collect(Collectors.toMap(FileItem::getFieldName, fi -> {
      try (InputStream is = fi.getInputStream()) {
        return IOUtils.toString(is, "UTF-8");
      } catch (Exception e) {
        return "";
      }
    }));
  }
}
