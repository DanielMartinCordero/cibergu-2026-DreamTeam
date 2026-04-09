package com.cibergu.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Record DTO para peticiones de registro.
 * Seguridad: Validación de patrón anti-XSS e Inyección.
 */
public record RegisterRequest(
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,15}$", message = "El username debe tener entre 4 y 15 caracteres alfanuméricos")
    String username
) {}
