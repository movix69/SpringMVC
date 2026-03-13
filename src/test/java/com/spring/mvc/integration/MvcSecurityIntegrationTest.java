package com.spring.mvc.integration;

import com.hibernate.config.DataInitializer;
import com.hibernate.entity.*;
import com.hibernate.repository.IGeneroRepository;
import com.hibernate.repository.IInterpreteRepository;
import com.hibernate.repository.IRolRepository;
import com.spring.mvc.service.PeliculaService;
import com.spring.mvc.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MvcSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private IRolRepository rolRepository;

    @MockitoBean
    private PeliculaService peliculaService;

    @MockitoBean
    private IGeneroRepository generoRepository;

    @MockitoBean
    private IInterpreteRepository interpreteRepository;

    @MockitoBean
    private DataInitializer dataInitializer;

    @Test
    void login_deberiaSerPublico() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registro_deberiaSerPublico() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"));
    }

    @Test
    void registroPost_ok_deberiaRetornarLogin() throws Exception {
        Usuario nuevo = new Usuario();
        Rol rol = new Rol();
        rol.setNombre("VIEWER");
        when(usuarioService.registrar(org.mockito.ArgumentMatchers.any(Usuario.class))).thenReturn(nuevo);
        when(rolRepository.findByNombre("VIEWER")).thenReturn(Optional.of(rol));

        mockMvc.perform(post("/registro")
                        .param("username", "user1")
                        .param("password", "password")
                        .param("nombre", "Usuario Uno")
                        .param("email", "user1@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("success"));

        verify(usuarioService).actualizar(nuevo);
    }

    @Test
    void registroPost_error_deberiaRetornarRegistro() throws Exception {
        when(usuarioService.registrar(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .thenThrow(new Exception("error"));

        mockMvc.perform(post("/registro")
                        .param("username", "user1")
                        .param("password", "password")
                        .param("nombre", "Usuario Uno")
                        .param("email", "user1@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void peliculas_sinAuth_deberiaRedirigirALogin() throws Exception {
        mockMvc.perform(get("/peliculas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void peliculas_conViewer_deberiaPermitirAcceso() throws Exception {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo("Matrix");
        pelicula.setDirector("Wachowski");
        pelicula.setAnio(1999);
        pelicula.setImagen("https://example.com/img.jpg");
        pelicula.setUrl("https://example.com");
        pelicula.setGenero(new Genero("Accion"));

        when(peliculaService.listar("", 0, "titulo", 5))
                .thenReturn(new PageImpl<>(List.of(pelicula)));

        mockMvc.perform(get("/peliculas")
                        .with(SecurityMockMvcRequestPostProcessors.user("viewer").roles("VIEWER")))
                .andExpect(status().isOk())
                .andExpect(view().name("peliculas"))
                .andExpect(model().attributeExists("peliculas"));
    }

    @Test
    void peliculasNuevo_conViewer_deberiaSerForbidden() throws Exception {
        mockMvc.perform(get("/peliculas/nuevo")
                        .with(SecurityMockMvcRequestPostProcessors.user("viewer").roles("VIEWER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void peliculasNuevo_conEditor_deberiaPermitirAcceso() throws Exception {
        when(generoRepository.findAll()).thenReturn(List.of(new Genero("Accion")));
        when(interpreteRepository.findAll()).thenReturn(List.of(new Interprete("Actor", 1980, "ES")));

        mockMvc.perform(get("/peliculas/nuevo")
                        .with(SecurityMockMvcRequestPostProcessors.user("editor").roles("EDITOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("peliculas-form"))
                .andExpect(model().attributeExists("pelicula", "generos", "interpretes"));
    }

    @Test
    void perfil_conAuth_deberiaRetornarPerfil() throws Exception {
        Usuario usuario = new Usuario();
        when(usuarioService.obtenerPorUsername("user1")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/perfil")
                        .with(SecurityMockMvcRequestPostProcessors.user("user1").roles("VIEWER")))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attributeExists("usuario"));
    }
}
