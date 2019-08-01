module sit.wt.app {
  requires java.xml;
  requires jdk.unsupported;
  requires java.sql;
  requires java.naming;
  requires java.desktop;
  requires java.management;
  requires java.security.jgss;
  requires java.instrument;
  requires javafx.web;
  requires javafx.fxml;
  requires java.annotation;
  requires spring.context;
  requires spring.core;
  requires spring.beans;
  requires lombok;
  requires commons.io;
  requires commons.lang3;
  requires controlsfx;
  requires selenium.support;
  requires logback.core;
  requires slf4j.api;

  requires sit.wt.util;
  requires sit.wt.runtime;
  requires sit.util.bth;

  opens io.sitoolkit.wt.gui.pres to javafx.fxml, spring.core;
  opens io.sitoolkit.wt.gui.infra.config to spring.core, spring.beans, spring.context;
  opens io.sitoolkit.wt.gui.app.script to spring.core;
  opens io.sitoolkit.wt.gui.app.sample to spring.core;
  opens io.sitoolkit.wt.gui.app.project to spring.core;
  opens io.sitoolkit.wt.gui.app.diffevidence to spring.core;
  opens io.sitoolkit.wt.gui.app.test to spring.core;
  opens io.sitoolkit.wt.gui.app.update to spring.core;

  exports io.sitoolkit.wt.gui.pres;
}
