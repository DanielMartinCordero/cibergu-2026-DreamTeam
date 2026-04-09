package com.cibergu.api.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * Record DTO para peticiones de autenticación.
 * Seguridad: Validación de campos obligatorios.
 */
public record AuthRequest(
    @NotBlank(message = "El nombre de usuario es obligatorio")
    String username,
    
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
