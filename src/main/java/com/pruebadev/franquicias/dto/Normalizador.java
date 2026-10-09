package com.pruebadev.franquicias.dto;

final class Normalizador {

    private Normalizador() {
    }

    static String recortar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
