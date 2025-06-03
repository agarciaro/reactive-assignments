package com.curso.reactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Java Reactive Programming")
                        .description("This application exposes APIs for learning reactive programming.")
                        .version("1.0"))
                .addServersItem(new Server()
                        .url("http://localhost:7070")
                        .description("Generated server url"));
    }
} 