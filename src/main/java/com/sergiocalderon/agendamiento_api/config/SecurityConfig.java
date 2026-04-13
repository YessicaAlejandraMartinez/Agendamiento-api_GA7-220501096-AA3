package com.sergiocalderon.agendamiento_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication
    .UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors
    .UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFiltro jwtFiltro;

    /* Configuración de CORS para Spring Security */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:3001"
        ));
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT",
            "PATCH", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors
                .configurationSource(
                    corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                /* Swagger — público */
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/v3/api-docs/**"
                ).permitAll()

                /* Imágenes — públicas */
                .requestMatchers(
                    "/uploads/**"
                ).permitAll()

                /* OPTIONS preflight — siempre permitido */
                .requestMatchers(
                    HttpMethod.OPTIONS, "/**"
                ).permitAll()

                /* Login y registro — públicos */
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/usuarios/login",
                    "/api/usuarios"
                ).permitAll()

                /* GET vestidos y disponibilidad — públicos */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/vestidos",
                    "/api/vestidos/**",
                    "/api/disponibilidad/disponibles/**"
                ).permitAll()

                /* Todo lo demás requiere autenticación */
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFiltro,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}