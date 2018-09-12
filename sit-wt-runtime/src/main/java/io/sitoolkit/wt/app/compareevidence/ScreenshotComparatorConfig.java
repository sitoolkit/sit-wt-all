package io.sitoolkit.wt.app.compareevidence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.sitoolkit.wt.app.config.BaseConfig;

@Configuration
@Import(BaseConfig.class)
public class ScreenshotComparatorConfig {

    @Bean
    public ScreenshotComparator ScreenshotComparator() {
        return new ScreenshotComparator();
    }
}
