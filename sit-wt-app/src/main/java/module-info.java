module sit.wt.app {
  requires commons.io;
  requires commons.lang3;
  requires controlsfx;
  requires javafx.web;
  requires javafx.fxml;
  requires java.annotation;
  requires spring.context;
  requires spring.core;
  requires spring.beans;
  requires selenium.support;
  requires lombok;
  requires org.mapstruct;
  requires org.mapstruct.processor;
  requires sit.wt.util;
  requires sit.wt.runtime;
  requires sit.util.bth;
  requires logback.core;
  requires slf4j.api;

  requires java.xml;
  requires jdk.unsupported;
  requires java.sql;
  requires java.naming;
  requires java.desktop;
  requires java.management;
  requires java.security.jgss;
  requires java.instrument;


  opens io.sitoolkit.wt.gui.pres to javafx.fxml;

  exports io.sitoolkit.wt.gui.pres;
}
