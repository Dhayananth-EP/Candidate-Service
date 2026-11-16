package com.recruitcrm.candidate_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI candidateServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Candidate Management Service API")
                        .description("REST API for managing, searching, and filtering candidate profiles with automated validations.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Engineering Candidate")
                                .email("dev@recruitcrm.io"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}