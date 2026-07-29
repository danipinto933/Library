package com.library.authservice.security;

import com.library.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación de UserDetailsService para Spring Security.
 * Carga el usuario desde la base de datos de usuarios por su userName.
 */
@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.library.authservice.model.User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario '" + username + "' no encontrado");
        }

        return new UserDetail(user);
    }
}
