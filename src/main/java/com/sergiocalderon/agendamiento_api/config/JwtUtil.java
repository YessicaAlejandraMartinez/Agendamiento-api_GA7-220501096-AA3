package com.sergiocalderon.agendamiento_api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secreto;

    @Value("${jwt.expiration}")
    private long expiracion;

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secreto.getBytes());
    }

    /* Genera un token JWT para el usuario */
    public String generarToken(String email, String rol,
            Integer idUsuario) {
        return Jwts.builder()
            .subject(email)
            .claim("rol", rol)
            .claim("idUsuario", idUsuario)
            .issuedAt(new Date())
            .expiration(new Date(
                System.currentTimeMillis() + expiracion))
            .signWith(obtenerClave())
            .compact();
    }

    /* Extrae el email del token */
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    /* Extrae el rol del token */
    public String obtenerRol(String token) {
        return obtenerClaims(token)
            .get("rol", String.class);
    }

    /* Extrae el ID del usuario del token */
    public Integer obtenerIdUsuario(String token) {
        return obtenerClaims(token)
            .get("idUsuario", Integer.class);
    }

    /* Valida si el token es válido */
    public boolean validarToken(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /* Extrae todos los claims del token */
    private Claims obtenerClaims(String token) {
        return Jwts.parser()
            .verifyWith(obtenerClave())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}