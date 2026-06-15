package com.DaniCRUD.fullStackBackend.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.DaniCRUD.fullStackBackend.service.UserServiceImpl;

@Component
public class CustomAuthenticationManager implements AuthenticationManager
{
    private UserServiceImpl userService;
    private BCryptPasswordEncoder bCrypt;

    public CustomAuthenticationManager(UserServiceImpl userService, BCryptPasswordEncoder bCrypt)
    {
        this.userService = userService;
        this.bCrypt = bCrypt;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        UserDetail user = (UserDetail) userService.loadUserByUsername(authentication.getName());

        if(!bCrypt.matches(authentication.getCredentials().toString(), user.getPassword())) 
            throw new BadCredentialsException("Credenciales inválidas");

        return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword(), user.getAuthorities());
    }
}
