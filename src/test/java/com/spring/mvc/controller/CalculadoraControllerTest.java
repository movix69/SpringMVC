package com.spring.mvc.controller;

import com.spring.mvc.service.CalculadoraService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraControllerTest {

    private final CalculadoraController controller = new CalculadoraController(new CalculadoraService());

    @Test
    void sumar_deberiaRetornarVistaResultadoConAtributoAppResul() {
        Model model = new ExtendedModelMap();

        String view = controller.sumar(model, 6, 4);

        assertEquals("resultado", view);
        assertEquals(10, model.getAttribute("appResul"));
    }
}

