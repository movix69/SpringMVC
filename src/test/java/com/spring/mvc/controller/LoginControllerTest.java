package com.spring.mvc.controller;

import com.hibernate.entity.Rol;
import com.hibernate.entity.Usuario;
import com.hibernate.repository.IRolRepository;
import com.spring.mvc.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private IRolRepository rolRepository;

    private LoginController controller;

    @BeforeEach
    void setUp() {
        controller = new LoginController();
        ReflectionTestUtils.setField(controller, "usuarioService", usuarioService);
        ReflectionTestUtils.setField(controller, "rolRepository", rolRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_deberiaRetornarVistaLoginYModeloBase() {
        Model model = new ExtendedModelMap();

        String view = controller.login(null, null, model);

        assertEquals("login", view);
        assertNotNull(model.getAttribute("usuario"));
    }

    @Test
    void login_deberiaAgregarMensajesSiHayParametros() {
        Model model = new ExtendedModelMap();

        String view = controller.login("true", "true", model);

        assertEquals("login", view);
        assertEquals("Usuario o contraseña incorrectos", model.getAttribute("error"));
        assertEquals("Has cerrado sesión correctamente", model.getAttribute("logout"));
    }

    @Test
    void registro_deberiaRetornarVistaRegistro() {
        Model model = new ExtendedModelMap();

        String view = controller.registro(model);

        assertEquals("registro", view);
        assertNotNull(model.getAttribute("usuario"));
    }

    @Test
    void registroPost_conErrores_deberiaRetornarRegistro() throws Exception {
        Usuario usuario = new Usuario();
        Model model = new ExtendedModelMap();
        BindingResult result = new BeanPropertyBindingResult(usuario, "usuario");
        result.rejectValue("username", "NotBlank", "Requerido");

        String view = controller.registroPost(usuario, result, model);

        assertEquals("registro", view);
        verify(usuarioService, never()).registrar(usuario);
    }

    @Test
    void registroPost_ok_deberiaAsignarRolYRetornarLogin() throws Exception {
        Usuario usuario = new Usuario();
        Usuario nuevo = new Usuario();
        Model model = new ExtendedModelMap();
        BindingResult result = new BeanPropertyBindingResult(usuario, "usuario");
        Rol rol = new Rol();
        rol.setNombre("VIEWER");

        when(usuarioService.registrar(usuario)).thenReturn(nuevo);
        when(rolRepository.findByNombre("VIEWER")).thenReturn(Optional.of(rol));

        String view = controller.registroPost(usuario, result, model);

        verify(usuarioService).registrar(usuario);
        verify(usuarioService).actualizar(nuevo);
        assertEquals("login", view);
        assertEquals("Registro completado. Por favor inicia sesión.", model.getAttribute("success"));
    }

    @Test
    void index_conUsuarioAutenticado_deberiaRedirigirPeliculas() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "usuario",
                        "pass",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_VIEWER"))
                )
        );
        Model model = new ExtendedModelMap();

        String view = controller.index(model);

        assertEquals("redirect:/peliculas", view);
    }

    @Test
    void index_conAnonymous_deberiaRedirigirLogin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", "pass")
        );
        Model model = new ExtendedModelMap();

        String view = controller.index(model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void perfil_conUsuarioExistente_deberiaRetornarPerfil() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", "pass")
        );
        Usuario usuario = new Usuario();
        when(usuarioService.obtenerPorUsername("user1")).thenReturn(Optional.of(usuario));
        Model model = new ExtendedModelMap();

        String view = controller.perfil(model);

        assertEquals("perfil", view);
        assertEquals(usuario, model.getAttribute("usuario"));
    }

    @Test
    void perfil_sinUsuario_deberiaRedirigirLogin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", "pass")
        );
        when(usuarioService.obtenerPorUsername("user1")).thenReturn(Optional.empty());
        Model model = new ExtendedModelMap();

        String view = controller.perfil(model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void actualizarPerfil_conErrores_deberiaRetornarPerfil() {
        Usuario usuario = new Usuario();
        Model model = new ExtendedModelMap();
        BindingResult result = new BeanPropertyBindingResult(usuario, "usuario");
        result.rejectValue("nombre", "NotBlank", "Requerido");

        String view = controller.actualizarPerfil(usuario, result, model);

        assertEquals("perfil", view);
        verify(usuarioService, never()).actualizar(usuario);
    }

    @Test
    void actualizarPerfil_ok_deberiaActualizarYRetornarPerfil() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", "pass")
        );
        Usuario actualizado = new Usuario();
        actualizado.setNombre("Nuevo Nombre");
        actualizado.setEmail("nuevo@mail.com");

        Usuario existente = new Usuario();
        when(usuarioService.obtenerPorUsername("user1")).thenReturn(Optional.of(existente));

        Model model = new ExtendedModelMap();
        BindingResult result = new BeanPropertyBindingResult(actualizado, "usuario");

        String view = controller.actualizarPerfil(actualizado, result, model);

        verify(usuarioService).actualizar(existente);
        assertEquals("perfil", view);
        assertEquals("Perfil actualizado correctamente", model.getAttribute("success"));
        assertEquals(existente, model.getAttribute("usuario"));
        assertEquals("Nuevo Nombre", existente.getNombre());
        assertEquals("nuevo@mail.com", existente.getEmail());
    }
}
