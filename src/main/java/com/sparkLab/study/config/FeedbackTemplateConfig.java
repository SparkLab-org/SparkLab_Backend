package com.sparkLab.study.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(FeedbackTemplateProperties.class)
@PropertySource(value = "classpath:feedback-templates.yml", factory = YamlPropertySourceFactory.class)
public class FeedbackTemplateConfig {
}
