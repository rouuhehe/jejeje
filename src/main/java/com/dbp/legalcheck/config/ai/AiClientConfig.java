package com.dbp.legalcheck.config.ai;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiClientConfig {

    private final AiConfig config;

    public AiClientConfig(AiConfig config) {
        this.config = config;
    }

    @Bean
    public ChatCompletionsClient chatCompletionsClient() {
        return new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(config.getToken()))
                .endpoint(config.getEndpoint())
                .buildClient();
    }
}