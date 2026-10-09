package com.pruebadev.franquicias.dto;

import com.pruebadev.franquicias.entity.Producto;

public record ProductoResponse(Long id, String nombre, int stock, Long sucursalId) {

    public static ProductoResponse of(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getStock(),
                producto.getSucursal().getId());
    }
}
