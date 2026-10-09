package com.pruebadev.franquicias.dto;

import com.pruebadev.franquicias.entity.Sucursal;

public record SucursalResponse( Long id, String nombre, Long franquiciaId) {

    public static SucursalResponse of(Sucursal sucursal) {
        return new SucursalResponse(sucursal.getId(), sucursal.getNombre(), sucursal.getFranquicia().getId());
    } 
}
