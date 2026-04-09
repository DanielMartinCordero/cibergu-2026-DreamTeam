package com.cibergu.api.security;

import com.cibergu.api.models.UserEntity;
import com.cibergu.api.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar los detalles del usuario desde la BD.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // En Java 25 utilizamos var por inferencia de tipos local.
        var userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("Usuario no encontrado en la base de datos");
        }

        /* 
         * Se construye y devuelve la implementación org.springframework.security.core.userdetails.User 
         * usando los datos obtenidos de la BD. 
         * Se asume que UserEntity cuenta con getters estándar (getUsername() y getPassword()).
         */
        return User.withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities("USER") // Rol por defecto
                .build();
    }
}
