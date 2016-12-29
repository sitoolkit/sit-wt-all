package org.sitoolkit.wt.infra.template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateEngineVelocityImpl implements TemplateEngine {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateEngineVelocityImpl.class);

    private Map<String, Template> templateCache = new HashMap<>();

    private String velocityProperties = "/velocity.properties";

    @PostConstruct
    public void init() {
        Properties prop = PropertyUtils.load(velocityProperties, false);
        Velocity.init(prop);
    }

    @Override
    public void write(TemplateModel model) {

        String str = writeToString(model);

        File outdir = new File(model.getOutDir());
        if (!outdir.exists()) {
            outdir.mkdirs();
        }

        File file = new File(outdir, model.getFileBase() + "." + model.getFileExt());
        try {
            FileUtils.writeStringToFile(file, str, "UTF-8");
            LOG.info("ファイルに書き込みました {}", file.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String writeToString(TemplateModel model) {

        Template template = templateCache.get(model.getTemplate());
        if (template == null) {
            template = Velocity.getTemplate(model.getTemplate());
        }

        VelocityContext context = new VelocityContext();
        context.put(model.getVar(), model);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }

    public String getVelocityProperties() {
        return velocityProperties;
    }

    public void setVelocityProperties(String velocityProperties) {
        this.velocityProperties = velocityProperties;
    }

}
