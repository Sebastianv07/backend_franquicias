package com.pruebadev.franquicias.dto;

import com.pruebadev.franquicias.entity.Franquicia;

public record FranquiciaResponse( Long id, String nombre) {

    public static FranquiciaResponse of(Franquicia franquicia) {
        return new FranquiciaResponse(franquicia.getId(), franquicia.getNombre());
        }
}
