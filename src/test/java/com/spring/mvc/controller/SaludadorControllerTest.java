package com.spring.mvc.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SaludadorControllerTest {

    private final SaludadorController controller = new SaludadorController();

    @Test
    void saludar_deberiaRetornarVistaSinModificarModelo() {
        Model model = new ExtendedModelMap();

        String view = controller.saludar(model);

        assertEquals("saludador", view);
        assertNull(model.getAttribute("appName"));
    }

    @Test
    void otro_deberiaRetornarVistaConMensajePersonalizado() {
        Model model = new ExtendedModelMap();

        String view = controller.otro(model);

        assertEquals("saludador", view);
        assertEquals("cambio el mensaje por defecto", model.getAttribute("appName"));
    }
}

