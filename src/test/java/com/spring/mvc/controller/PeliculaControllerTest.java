package com.spring.mvc.controller;

import com.hibernate.entity.Genero;
import com.hibernate.entity.Interprete;
import com.hibernate.entity.Pelicula;
import com.hibernate.repository.IGeneroRepository;
import com.hibernate.repository.IInterpreteRepository;
import com.spring.mvc.service.PeliculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculaControllerTest {

    @Mock
    private PeliculaService service;

    @Mock
    private IGeneroRepository generoRepo;

    @Mock
    private IInterpreteRepository interpreteRepo;

    private PeliculaController controller;

    @BeforeEach
    void setUp() {
        controller = new PeliculaController();
        controller.service = service;
        controller.generoRepo = generoRepo;
        controller.interpreteRepo = interpreteRepo;
    }

    @Test
    void listar_deberiaCargarModeloYRetornarVistaPeliculas() {
        Page<Pelicula> page = new PageImpl<>(List.of(new Pelicula()));
        when(service.listar("matrix", 1, "titulo", 8)).thenReturn(page);
        Model model = new ExtendedModelMap();

        String view = controller.listar("matrix", 1, "titulo", 8, model);

        assertEquals("peliculas", view);
        assertEquals(page, model.getAttribute("peliculas"));
        assertEquals("matrix", model.getAttribute("search"));
        assertEquals("titulo", model.getAttribute("sort"));
        assertEquals(8, model.getAttribute("size"));
    }

    @Test
    void nuevo_deberiaCargarCatalogosYObjetoPelicula() {
        List<Genero> generos = List.of(new Genero("Accion"));
        List<Interprete> interpretes = List.of(new Interprete("Actor", 1980, "ES"));
        when(generoRepo.findAll()).thenReturn(generos);
        when(interpreteRepo.findAll()).thenReturn(interpretes);
        Model model = new ExtendedModelMap();

        String view = controller.nuevo(model);

        assertEquals("peliculas-form", view);
        assertNotNull(model.getAttribute("pelicula"));
        assertEquals(generos, model.getAttribute("generos"));
        assertEquals(interpretes, model.getAttribute("interpretes"));
    }

    @Test
    void editar_deberiaCargarPeliculaYCatalogos() {
        Pelicula pelicula = new Pelicula();
        List<Genero> generos = List.of(new Genero("Drama"));
        List<Interprete> interpretes = List.of(new Interprete("Actriz", 1990, "AR"));
        when(service.get(5)).thenReturn(pelicula);
        when(generoRepo.findAll()).thenReturn(generos);
        when(interpreteRepo.findAll()).thenReturn(interpretes);
        Model model = new ExtendedModelMap();

        String view = controller.editar(5, model);

        assertEquals("peliculas-form", view);
        assertEquals(pelicula, model.getAttribute("pelicula"));
        assertEquals(generos, model.getAttribute("generos"));
        assertEquals(interpretes, model.getAttribute("interpretes"));
    }

    @Test
    void guardar_deberiaDelegarEnServiceYRedirigir() {
        Pelicula pelicula = new Pelicula();
        Model model = new ExtendedModelMap();
        BindingResult result = new BeanPropertyBindingResult(pelicula, "pelicula");

        String view = controller.guardar(pelicula, result, model);

        verify(service).guardar(pelicula);
        assertEquals("redirect:/peliculas", view);
    }

    @Test
    void guardar_deberiaRetornarFormularioSiHayErrores() {
        Pelicula pelicula = new Pelicula();
        Model model = new ExtendedModelMap();
        BindingResult result = new BeanPropertyBindingResult(pelicula, "pelicula");
        result.rejectValue("titulo", "NotBlank", "El titulo es obligatorio");
        List<Genero> generos = List.of(new Genero("Accion"));
        List<Interprete> interpretes = List.of(new Interprete("Actor", 1980, "ES"));
        when(generoRepo.findAll()).thenReturn(generos);
        when(interpreteRepo.findAll()).thenReturn(interpretes);

        String view = controller.guardar(pelicula, result, model);

        verify(service, never()).guardar(pelicula);
        assertEquals("peliculas-form", view);
        assertEquals(generos, model.getAttribute("generos"));
        assertEquals(interpretes, model.getAttribute("interpretes"));
    }

    @Test
    void borrar_deberiaDelegarEnServiceYRedirigir() {
        String view = controller.borrar(3);

        verify(service).borrar(3);
        assertEquals("redirect:/peliculas", view);
    }
}
