package com.sergiocalderon.agendamiento_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * Configuración de Swagger / OpenAPI
 * Genera documentación automática de todos los endpoints
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sergio Calderón Atelier — API REST")
                .version("1.0.0")
                .description(
                    "API REST del Sistema de Agendamiento de Citas " +
                    "para el Atelier de Modas Sergio Calderón. " +
                    "Desarrollada con Spring Boot 3.5 como parte " +
                    "del proyecto formativo SENA — " +
                    "Análisis y Desarrollo de Software."
                )
                
            )
            .servers(List.of(
                new Server()
                    .url("http://localhost:8081")
                    .description("Servidor de desarrollo local")
            ));
    }
}
