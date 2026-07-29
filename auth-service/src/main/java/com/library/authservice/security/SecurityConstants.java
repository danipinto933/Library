package com.library.authservice.security;

/**
 * Constantes de seguridad JWT — idénticas al monolito para garantizar
 * compatibilidad total con los tokens ya emitidos.
 */
public class SecurityConstants {

    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 días en ms

    private SecurityConstants() {}
}
