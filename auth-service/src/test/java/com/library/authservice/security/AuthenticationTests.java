package com.library.authservice.security;

import com.library.authservice.model.Role;
import com.library.authservice.model.User;
import com.library.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios del proceso de autenticación.
 *
 * Cobertura:
 *   - Login correcto → Authentication con roles correctos
 *   - Password incorrecta → BadCredentialsException
 *   - Usuario inexistente → UsernameNotFoundException
 *   - Generación correcta de authorities con prefijo ROLE_
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationTests {

    @Mock
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCrypt;
    private AuthUserDetailsService userDetailsService;
    private CustomAuthenticationManager authManager;

    @BeforeEach
    void setUp() {
        bCrypt = new BCryptPasswordEncoder();
        userDetailsService = new AuthUserDetailsService(userRepository);
        authManager = new CustomAuthenticationManager(userDetailsService, bCrypt);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private User buildUser(String userName, String rawPassword, String roleName) {
        Role role = new Role(1L, roleName);
        return new User(1L, userName, "Test User", "test@email.com",
                bCrypt.encode(rawPassword), role);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Autenticación exitosa
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void authenticate_credencialesCorrectas_retornaAuthenticationConRoles() {
        String rawPassword = "mypassword";
        User user = buildUser("dani", rawPassword, "ADMIN");
        when(userRepository.findByUserNameIgnoreCase("dani")).thenReturn(user);

        Authentication result = authManager.authenticate(
                new UsernamePasswordAuthenticationToken("dani", rawPassword)
        );

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getName()).isEqualTo("dani");
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_ADMIN");
    }

    @Test
    void authenticate_usuarioConRolUser_retornaRoleUser() {
        String rawPassword = "pass123";
        User user = buildUser("usuario", rawPassword, "USER");
        when(userRepository.findByUserNameIgnoreCase("usuario")).thenReturn(user);

        Authentication result = authManager.authenticate(
                new UsernamePasswordAuthenticationToken("usuario", rawPassword)
        );

        assertThat(result.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_USER");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Credenciales incorrectas
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void authenticate_passwordIncorrecta_lanzaBadCredentialsException() {
        User user = buildUser("dani", "correctPassword", "ADMIN");
        when(userRepository.findByUserNameIgnoreCase("dani")).thenReturn(user);

        assertThatThrownBy(() ->
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken("dani", "wrongPassword")
                )
        ).isInstanceOf(BadCredentialsException.class)
         .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    void authenticate_passwordVacia_lanzaBadCredentialsException() {
        User user = buildUser("dani", "somePassword", "ADMIN");
        when(userRepository.findByUserNameIgnoreCase("dani")).thenReturn(user);

        assertThatThrownBy(() ->
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken("dani", "")
                )
        ).isInstanceOf(BadCredentialsException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Usuario no encontrado
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void authenticate_usuarioNoExiste_lanzaUsernameNotFoundException() {
        when(userRepository.findByUserNameIgnoreCase("fantasma")).thenReturn(null);

        assertThatThrownBy(() ->
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken("fantasma", "cualquiera")
                )
        ).isInstanceOf(UsernameNotFoundException.class)
         .hasMessageContaining("fantasma");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — UserDetail
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void userDetail_construyeAuthorityConPrefijoRole() {
        Role role = new Role(1L, "ADMIN");
        User user = new User(1L, "dani", "Test", "test@test.com", "hash", role);
        UserDetail userDetail = new UserDetail(user);

        assertThat(userDetail.getUsername()).isEqualTo("dani");
        assertThat(userDetail.getAuthorities()).hasSize(1);
        assertThat(userDetail.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_ADMIN");
        assertThat(userDetail.isAccountNonExpired()).isTrue();
        assertThat(userDetail.isEnabled()).isTrue();
    }
}
