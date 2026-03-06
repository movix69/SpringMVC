package com.spring.mvc.service;

import com.hibernate.entity.Pelicula;
import com.hibernate.repository.IPeliculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeliculaServiceTest {

    @Mock
    private IPeliculaRepository repo;

    private PeliculaService service;

    @BeforeEach
    void setUp() {
        service = new PeliculaService();
        service.repo = repo;
    }

    @Test
    void listar_deberiaUsarSizeMinimoUnoYSortSolicitado() {
        Page<Pelicula> expectedPage = new PageImpl<>(List.of(new Pelicula()));
        when(repo.buscar(org.mockito.ArgumentMatchers.eq("matrix"), org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Pelicula> result = service.listar("matrix", 2, "titulo", 0);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).buscar(org.mockito.ArgumentMatchers.eq("matrix"), captor.capture());

        Pageable pageable = captor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(1, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("titulo").getDirection());
        assertEquals(expectedPage, result);
    }

    @Test
    void get_deberiaRetornarPeliculaSiExiste() {
        Pelicula pelicula = new Pelicula();
        when(repo.findById(10)).thenReturn(Optional.of(pelicula));

        Pelicula result = service.get(10);

        assertEquals(pelicula, result);
    }

    @Test
    void get_deberiaLanzarExcepcionSiNoExiste() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.get(99));
    }

    @Test
    void guardar_deberiaDelegarEnRepositorio() {
        Pelicula pelicula = new Pelicula();
        when(repo.save(pelicula)).thenReturn(pelicula);

        Pelicula result = service.guardar(pelicula);

        verify(repo).save(pelicula);
        assertEquals(pelicula, result);
    }

    @Test
    void borrar_deberiaDelegarEnRepositorio() {
        service.borrar(7);
        verify(repo).deleteById(7);
    }
}

