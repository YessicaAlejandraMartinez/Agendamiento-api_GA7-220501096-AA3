package com.sergiocalderon.agendamiento_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para servir imágenes del catálogo
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/catalogo}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {
        /* Sirve las imágenes en /uploads/** */
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(
                "file:" + uploadDir + "/"
            );

        /* CORS para las imágenes */
        registry.addResourceHandler("/api/imagenes/**")
            .addResourceLocations(
                "file:" + uploadDir + "/"
            );
    }
}