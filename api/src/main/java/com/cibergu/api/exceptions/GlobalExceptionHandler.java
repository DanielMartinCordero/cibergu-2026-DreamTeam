package com.cibergu.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones (GlobalExceptionHandler).
 * Seguridad: Punto único de control de errores para prevenir Information Leakage y Anti-Recon.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Seguridad: Retorna siempre un HTTP 401 con mensaje genérico ("Credenciales inválidas")
     * frente a cualquier fallo de autenticación (BadCredentialsException) para mitigar enumeración de usuarios.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas"));
    }

    /**
     * Seguridad: Captura fallos de validación perimetral (MethodArgumentNotValidException) y 
     * oscurece detalles específicos lanzando un HTTP 400 estandarizado.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Error de validación en la solicitud"));
    }

    /**
     * Seguridad: Atrapa cualquier otra excepción no manejada para evitar mostrar Stacktraces al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
    }
}
