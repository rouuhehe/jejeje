package com.dbp.legalcheck.config.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {
    private String token;
    private String endpoint;
    private String model;
}
