package io.sitoolkit.wt.app.script2java;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.domain.testclass.TestClass;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
@Import(BaseConfig.class)
public class Script2JavaConfig {

    @Resource
    @Bean
    public Script2Java getTestClassGenerator(TestScriptDao dao, TemplateEngine templateEngine) {
        Script2Java gen = new Script2Java();
        gen.setDao(dao);
        gen.setTemplateEngine(templateEngine);
        return gen;
    }

    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine engine = new TemplateEngineVelocityImpl();
        return engine;
    }

    @Bean
    @Scope("prototype")
    public TestScript getTestScript() {
        return new TestScript();
    }

    @Bean
    @Scope("prototype")
    public TestClass getTestClass() {
        return new TestClass();
    }
}
