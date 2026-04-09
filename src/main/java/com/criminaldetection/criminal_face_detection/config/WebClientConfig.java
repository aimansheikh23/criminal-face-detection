package com.criminaldetection.criminal_face_detection.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${python.service.url}")
    private String serviceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(serviceUrl).build();
    }
}
