/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.infra;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author yuichi.kuwahara
 */
// jettyのバグでアノテーションによる定義ではサーブレットがデプロイされない。
// web.xmlで定義する。
// @WebServlet(urlPatterns = "/fileupload")
// @MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class FileUploadServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    Part part = req.getPart("file");
    String content = IOUtils.toString(part.getInputStream(), "UTF-8");
    req.setAttribute("content", content);

    RequestDispatcher rd = req.getRequestDispatcher("/fileupload.jsp");
    rd.forward(req, resp);
  }

}
