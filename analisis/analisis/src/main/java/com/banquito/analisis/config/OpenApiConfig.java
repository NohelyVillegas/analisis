package com.espe.analisis.crediticio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Análisis Crediticio API")
                .version("1.0.0")
                .description("Microservicio para análisis de riesgo crediticio")
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("desarrollo@espe.edu.ec"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")));
    }
}