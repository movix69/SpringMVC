package com.spring.mvc.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraServiceTest {

    private final CalculadoraService calculadoraService = new CalculadoraService();

    @Test
    void sumar_deberiaRetornarResultadoCorrecto() {
        assertEquals(9, calculadoraService.sumar(4, 5));
    }
}

