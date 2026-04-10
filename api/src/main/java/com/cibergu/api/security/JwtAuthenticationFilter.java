package com.cibergu.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
// Extendemos OncePerRequestFilter para asegurar que la validación del token se
// ejecute exactamente una vez por petición
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraemos la cabecera 'Authorization' de la petición HTTP
        final String authHeader = request.getHeader("Authorization");

        // 2. Filtro de descarte temprano: Si no hay cabecera o no tiene el prefijo
        // 'Bearer ', ignoramos la validación.
        // La petición pasará al siguiente filtro y será denegada automáticamente si la
        // ruta requiere autenticación.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraemos el payload del token omitiendo la palabra "Bearer " (7
        // caracteres)
        final String jwt = authHeader.substring(7);
        // 4. Intentamos obtener el usuario leyendo el token
        final String username = jwtService.extractUsername(jwt);

        // 5. Verificamos que hemos extraído un usuario y que no existe ya una sesión
        // activa en este hilo de ejecución
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargamos el usuario desde la base de datos a través de nuestro
            // UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Validación criptográfica: Verificamos que la firma del token no haya sido
            // manipulada
            // y que coincida con el usuario obtenido de la base de datos
            if (username.equals(userDetails.getUsername()) && jwtService.validateToken(jwt)) {

                // 7. Si todo es correcto, generamos el "Pase de Seguridad" (Token de
                // Autenticación de Spring)
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                // Añadimos contexto extra a la autenticación (como la IP del cliente)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Inyectamos el usuario validado en el Contexto de Seguridad de Spring.
                // A partir de este momento, el framework reconoce la petición como autorizada.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continuamos con el flujo normal de la petición hacia el controlador
        filterChain.doFilter(request, response);
    }
}
