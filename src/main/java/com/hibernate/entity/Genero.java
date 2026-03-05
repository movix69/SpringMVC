package com.hibernate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_generos")
@Getter
@Setter
@NoArgsConstructor
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "genero")
    private List<Pelicula> peliculas = new ArrayList<>();

    public Genero(String nombre) {
        this.nombre = nombre;
    }
}
