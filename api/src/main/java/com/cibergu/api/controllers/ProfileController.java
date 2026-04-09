package com.cibergu.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de Perfil de Usuario.
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public ResponseEntity<String> getProfile() {
        // Seguridad: Se extrae el usuario de forma segura desde el SecurityContext, 
        // alimentado previamente por la validación del JWT de forma Stateless.
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        var username = authentication.getName();
        return ResponseEntity.ok("Perfil del usuario: " + username);
    }
}
