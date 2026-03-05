package com.hibernate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_interpretes")
@Getter
@Setter
@NoArgsConstructor
public class Interprete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    @Column(name = "anio_nac")
    private int anioNacimiento;

    private String nacionalidad;

    @ManyToMany(mappedBy = "reparto")
    private List<Pelicula> peliculas = new ArrayList<>();

    public Interprete(int id, String nombre, int anioNacimiento, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.anioNacimiento = anioNacimiento;
        this.nacionalidad = nacionalidad;
    }

    public Interprete(String nombre, int anioNacimiento, String nacionalidad) {
        this.nombre = nombre;
        this.anioNacimiento = anioNacimiento;
        this.nacionalidad = nacionalidad;
    }

    public int getAnio_nac() {
        return anioNacimiento;
    }

    public void setAnio_nac(int anio_nac) {
        this.anioNacimiento = anio_nac;
    }
}
