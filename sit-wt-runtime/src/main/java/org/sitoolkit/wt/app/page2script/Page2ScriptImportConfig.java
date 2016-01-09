package org.sitoolkit.wt.app.page2script;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(Page2ScriptConfig.class)
@Profile("debug")
public class Page2ScriptImportConfig {

}
