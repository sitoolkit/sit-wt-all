package org.sitoolkit.wt.app.compareevidence;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BaseConfig.class)
public class ScreenshotComparatorConfig {

    @Bean
    public ScreenshotComparator ScreenshotComparator() {
        return new ScreenshotComparator();
    }
}
