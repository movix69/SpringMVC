package com.spring.mvc.controller;

import com.hibernate.entity.Pelicula;
import com.hibernate.repository.IGeneroRepository;
import com.hibernate.repository.IInterpreteRepository;
import com.spring.mvc.service.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/peliculas")
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class PeliculaController {

    @Autowired
    PeliculaService service;

    @Autowired
    IGeneroRepository generoRepo;

    @Autowired
    IInterpreteRepository interpreteRepo;

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "titulo") String sort,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<Pelicula> peliculas = service.listar(search, page, sort, size);

        model.addAttribute("peliculas", peliculas);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        return "peliculas";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public String nuevo(Model model) {

        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("generos", generoRepo.findAll());
        model.addAttribute("interpretes", interpreteRepo.findAll());

        return "peliculas-form";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public String editar(@PathVariable int id, Model model) {

        model.addAttribute("pelicula", service.get(id));
        model.addAttribute("generos", generoRepo.findAll());
        model.addAttribute("interpretes", interpreteRepo.findAll());

        return "peliculas-form";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public String guardar(@Valid @ModelAttribute("pelicula") Pelicula pelicula, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("generos", generoRepo.findAll());
            model.addAttribute("interpretes", interpreteRepo.findAll());
            return "peliculas-form";
        }

        service.guardar(pelicula);

        return "redirect:/peliculas";
    }

    @GetMapping("/borrar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public String borrar(@PathVariable int id) {

        service.borrar(id);

        return "redirect:/peliculas";
    }

}
