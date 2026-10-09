package com.pruebadev.franquicias.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void nombreRequestEliminaEspaciosAlInicioYAlFinal() {
        assertThat(new NombreRequest("  Franquicia A  ").nombre()).isEqualTo("Franquicia A");
    }

    @Test
    void nombreRequestConservaNullParaQueLaValidacionLoRechace() {
        assertThat(new NombreRequest(null).nombre()).isNull();
    }

    @Test
    void productoRequestEliminaEspaciosDelNombre() {
        assertThat(new ProductoRequest(" Café ", 10)).isEqualTo(new ProductoRequest("Café", 10));
    }
}
