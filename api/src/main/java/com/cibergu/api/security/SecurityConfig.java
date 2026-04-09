package com.cibergu.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    // Inyección de dependencias mediante constructor (mejor práctica de Spring)
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desactivamos CSRF porque nuestra API usa tokens JWT en las cabeceras, no
                // cookies de sesión
                .csrf(AbstractHttpConfigurer::disable)
                // Establecemos la política como STATELESS. El servidor no guarda el estado del
                // usuario en memoria.
                // Esto garantiza una mayor escalabilidad y previene ataques de fijación de
                // sesión.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Definimos el control de acceso a los endpoints
                .authorizeHttpRequests(auth -> auth
                        // Las rutas de registro y login son de acceso público
                        .requestMatchers("/auth/**").permitAll()
                        // Cualquier otra petición a la API requiere un token válido
                        .anyRequest().authenticated())
                // Asignamos el proveedor de autenticación que conecta con nuestra base de datos
                // (SQLite)
                .authenticationProvider(authenticationProvider())
                // Insertamos nuestro filtro JWT personalizado justo ANTES del filtro estándar
                // de Spring.
                // Así interceptamos el token y autenticamos al usuario antes de que Spring
                // bloquee la petición.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Configura el proveedor que buscará los usuarios en la BD y comparará las
    // contraseñas
    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Expone el gestor de autenticación global de Spring Security
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // Algoritmo de hashing robusto para almacenar contraseñas de forma segura
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}