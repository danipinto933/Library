package com.library.authservice.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * AuthenticationManager personalizado — carga el usuario por username,
 * verifica la contraseña con BCrypt y devuelve el token de autenticación.
 */
@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private final AuthUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCrypt;

    public CustomAuthenticationManager(AuthUserDetailsService userDetailsService,
                                       BCryptPasswordEncoder bCrypt) {
        this.userDetailsService = userDetailsService;
        this.bCrypt = bCrypt;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());

        if (!bCrypt.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        return new UsernamePasswordAuthenticationToken(
                authentication.getName(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
