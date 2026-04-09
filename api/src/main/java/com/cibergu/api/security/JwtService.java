package com.cibergu.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Servicio de JWT para el concurso CIBERGU 2026.
 * Utiliza JJWT 0.12.5.
 */
@Service
public class JwtService {

    // Clave secreta estática de 256 bits (32 caracteres) requerida para la prueba
    private static final String SECRET_KEY_STRING = "C1b3rGu2026HackathonSecretKey123";
    private final SecretKey key;

    public JwtService() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT para el usuario proporcionado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                // Expira en 24 horas a modo de ejemplo
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el username (Subject) del token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valida si la firma del token es correcta y no ha expirado.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // El token es inválido, está expirado o fue alterado
            return false;
        }
    }

    /**
     * Extractor genérico de Claims.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();  // JJWT 0.12.5 usa getPayload() en vez de getBody()
        return claimsResolver.apply(claims);
    }
}
