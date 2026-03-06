package com.spring.mvc.service;

import com.hibernate.entity.Pelicula;
import com.hibernate.repository.IPeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class PeliculaService {

    @Autowired
    IPeliculaRepository repo;

    public Page<Pelicula> listar(String search, int page, String sort, int size) {
        int pageSize = Math.max(1, size);
        String sortProperty = switch (sort) {
            case "anio" -> "anio";
            case "interprete" -> "reparto.nombre";
            case "titulo" -> "titulo";
            default -> "titulo";
        };
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortProperty));
        return repo.buscar(search, pageable);
    }

    public Pelicula get(int id){
        return repo.findById(id).orElseThrow();
    }

    public Pelicula guardar(Pelicula p){
        return repo.save(p);
    }

    public void borrar(int id){
        repo.deleteById(id);
    }
}
