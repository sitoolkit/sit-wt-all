package org.sitoolkit.wt.infra.template;

public interface TemplateEngine {

    void write(TemplateModel model);

    String writeToString(TemplateModel model);
}
