package com.hibernate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_movies")
@Getter
@Setter
@NoArgsConstructor
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titulo;
    private String director;
    private String imagen;
    private String url;

    @Column(name = "año")
    private int anio;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "genero_id")
    private Genero genero;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "t_movies_interpretes",
            joinColumns = @JoinColumn(name = "pelicula_id"),
            inverseJoinColumns = @JoinColumn(name = "interprete_id")
    )
    private List<Interprete> reparto = new ArrayList<>();

    public Pelicula(String titulo, String director, int anio, String imagen, String url) {
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.imagen = imagen;
        this.url = url;
    }

    public void setReparto(List<Interprete> reparto) {
        this.reparto.forEach(interprete -> interprete.getPeliculas().remove(this));
        this.reparto.clear();
        if (reparto != null) {
            reparto.forEach(this::addInterprete);
        }
    }

    public void addInterprete(Interprete interprete) {
        if (!reparto.contains(interprete)) {
            reparto.add(interprete);
        }
        if (!interprete.getPeliculas().contains(this)) {
            interprete.getPeliculas().add(this);
        }
    }

    public void removeInterprete(Interprete interprete) {
        reparto.remove(interprete);
        interprete.getPeliculas().remove(this);
    }

    public void setGenero(Genero genero) {
        if (this.genero != null) {
            this.genero.getPeliculas().remove(this);
        }
        this.genero = genero;
        if (genero != null && !genero.getPeliculas().contains(this)) {
            genero.getPeliculas().add(this);
        }
    }
}
