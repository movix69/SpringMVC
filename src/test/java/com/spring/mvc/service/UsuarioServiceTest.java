package com.spring.mvc.service;

import com.hibernate.entity.Rol;
import com.hibernate.entity.Usuario;
import com.hibernate.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void loadUserByUsername_deberiaLanzarSiNoExiste() {
        when(usuarioRepository.findByUsername("nope")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nope"));
    }

    @Test
    void loadUserByUsername_deberiaLanzarSiInactivo() {
        Usuario usuario = new Usuario();
        usuario.setActivo(false);
        when(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario));

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("user"));
    }

    @Test
    void loadUserByUsername_deberiaConstruirUserDetailsConRoles() {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        usuario.setUsername("user");
        usuario.setPassword("hash");
        Rol rol = new Rol();
        rol.setNombre("ADMIN");
        usuario.setRoles(Set.of(rol));
        when(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario));

        var userDetails = service.loadUserByUsername("user");

        assertEquals("user", userDetails.getUsername());
        assertEquals("hash", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void registrar_deberiaRechazarUsernameDuplicado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("user");
        when(usuarioRepository.existsByUsername("user")).thenReturn(true);

        assertThrows(Exception.class, () -> service.registrar(usuario));
        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    void registrar_deberiaRechazarEmailDuplicado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("user");
        usuario.setEmail("mail@test.com");
        when(usuarioRepository.existsByUsername("user")).thenReturn(false);
        when(usuarioRepository.existsByEmail("mail@test.com")).thenReturn(true);

        assertThrows(Exception.class, () -> service.registrar(usuario));
        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    void registrar_deberiaEncriptarPasswordYActivar() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("user");
        usuario.setEmail("mail@test.com");
        usuario.setPassword("plain");
        when(usuarioRepository.existsByUsername("user")).thenReturn(false);
        when(usuarioRepository.existsByEmail("mail@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("hash");

        service.registrar(usuario);

        verify(passwordEncoder).encode("plain");
        verify(usuarioRepository).save(usuario);
        assertEquals("hash", usuario.getPassword());
        assertEquals(true, usuario.getActivo());
    }
}
