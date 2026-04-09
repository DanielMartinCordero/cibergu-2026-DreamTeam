package com.cibergu.api.controllers;

import com.cibergu.api.dtos.AuthRequest;
import com.cibergu.api.dtos.RegisterRequest;
import com.cibergu.api.security.JwtService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controlador de Autenticación.
 * Seguridad: Implementa prevención contra ataques de fuerza bruta mediante Rate Limiting (Bucket4j).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Almacenamiento en memoria para Rate Limiting basado en IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        // Refill de 5 unidades por minuto, limit actual 5 (Bucket4j API v8+)
        var limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        var ip = httpRequest.getRemoteAddr();
        var bucket = resolveBucket(ip);

        // Seguridad: Control de Rate Limiting por IP (Mitigación Fuerza Bruta)
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos. Inténtelo más tarde.");
        }

        // Delegamos a AuthenticationManager: arrojará BadCredentialsException si las credenciales fallan, 
        // siendo manejada por el GlobalExceptionHandler
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Generamos JWT de forma STATELESS
        var token = jwtService.generateToken(request.username());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        // La validación perimetral corre por @Valid
        // Como no se nos facilita persistencia, se retorna éxito
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito");
    }
}
