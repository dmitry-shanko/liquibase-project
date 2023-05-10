package com.liquibase.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonNullableModule jsonNullableModule(ObjectMapper objectMapper) {
        var module = new JsonNullableModule();
        objectMapper.registerModule(module);
        return module;
    }
}
