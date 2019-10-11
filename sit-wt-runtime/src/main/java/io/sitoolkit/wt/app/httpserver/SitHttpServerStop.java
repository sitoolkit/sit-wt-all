package io.sitoolkit.wt.app.httpserver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SitHttpServerStop {

  public static void main(String[] args) {

    try {
      URL url = new URL("http://localhost:9999");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setInstanceFollowRedirects(false);
      connection.connect();
      connection.getInputStream().close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
