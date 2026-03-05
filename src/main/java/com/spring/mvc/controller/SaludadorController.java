package com.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SaludadorController {
    @GetMapping("/")
    public String saludar(Model model){
        return "saludador";
    }
    @GetMapping("/otro")
    public String otro(Model model){
        model.addAttribute("appName","cambio el mensaje por defecto");
        return "saludador";
    }
}
