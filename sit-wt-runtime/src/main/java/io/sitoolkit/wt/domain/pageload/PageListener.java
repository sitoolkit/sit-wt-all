package io.sitoolkit.wt.domain.pageload;

public interface PageListener {

  void setUp();

  void setUpPage(PageContext ctx);

  void tearDown();

  void tearDownPage(PageContext ctx);

}
