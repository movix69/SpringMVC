package com.spring.mvc.controller;

import com.spring.mvc.service.CalculadoraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculadoraController {
    //sino queremos utilizar por contructor sino por inyeccion directa
    //@Autowired
    private final CalculadoraService calculadora;
    public CalculadoraController(CalculadoraService Calculadora) {
        this.calculadora = Calculadora;
    }
    @GetMapping("/sumar")
    public String sumar(Model model, @RequestParam int s1, @RequestParam int s2){
        int resultado = calculadora.sumar(s1,s2);
        model.addAttribute("appResul",resultado);
        return "resultado";
    }
}
