package com.hibernate.repository;

import com.hibernate.entity.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPeliculaRepository extends JpaRepository<Pelicula, Integer> {

    @Query("""
            SELECT p FROM Pelicula p
            LEFT JOIN p.genero g
            LEFT JOIN p.reparto i
            WHERE lower(p.titulo) LIKE lower(concat('%', :search, '%'))
               OR lower(g.nombre) LIKE lower(concat('%', :search, '%'))
               OR lower(i.nombre) LIKE lower(concat('%', :search, '%'))
               OR str(p.anio) LIKE concat('%', :search, '%')
            GROUP BY p.id, g.nombre
            """)
    Page<Pelicula> buscar(String search, Pageable pageable);

}
