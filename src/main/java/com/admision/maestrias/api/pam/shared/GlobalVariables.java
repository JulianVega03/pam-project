package com.admision.maestrias.api.pam.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalVariables {

    @Value("${app.baseurl}")
    private String baseUrl;

    @Bean
    public String getBaseUrl() {
        return baseUrl;
    }
}
