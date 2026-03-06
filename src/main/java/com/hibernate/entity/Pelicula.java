package com.hibernate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 255, message = "El titulo no puede superar 255 caracteres")
    private String titulo;

    @NotBlank(message = "El director es obligatorio")
    @Size(max = 255, message = "El director no puede superar 255 caracteres")
    private String director;

    @NotBlank(message = "La imagen es obligatoria")
    @Pattern(regexp = "https?://.+", message = "La imagen debe ser una URL valida (http/https)")
    private String imagen;

    @NotBlank(message = "La URL es obligatoria")
    @Pattern(regexp = "https?://.+", message = "La URL debe ser valida (http/https)")
    private String url;

    @Column(name = "año")
    @NotNull(message = "El ano es obligatorio")
    @Min(value = 1888, message = "El ano debe ser mayor o igual a 1888")
    @Max(value = 2100, message = "El ano debe ser menor o igual a 2100")
    private Integer anio;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "genero_id")
    @NotNull(message = "Debe seleccionar un genero")
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
