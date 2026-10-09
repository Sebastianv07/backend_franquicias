package com.pruebadev.franquicias.dto;

import com.pruebadev.franquicias.entity.Producto;

public record ProductoMayorStockResponse(Long sucursalId, String sucursalNombre, Long productoId, String productoNombre, int stock) {
    
    public static ProductoMayorStockResponse of(Producto producto) {
        return new ProductoMayorStockResponse(
            producto.getSucursal().getId(),
            producto.getSucursal().getNombre(),
            producto.getId(),
            producto.getNombre(),
            producto.getStock()
        );
    }
}
