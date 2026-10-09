package com.pruebadev.franquicias.service;

import org.springframework.test.util.ReflectionTestUtils;

import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Producto;
import com.pruebadev.franquicias.entity.Sucursal;

/**
 * Construye entidades con id para las pruebas, ya que el id lo asigna la base de datos.
 */
final class EntidadesPrueba {

    private EntidadesPrueba() {
    }

    static Franquicia franquicia(Long id, String nombre) {
        return conId(new Franquicia(nombre), id);
    }

    static Sucursal sucursal(Long id, String nombre, Franquicia franquicia) {
        return conId(new Sucursal(nombre, franquicia), id);
    }

    static Producto producto(Long id, String nombre, int stock, Sucursal sucursal) {
        return conId(new Producto(nombre, stock, sucursal), id);
    }

    static <T> T conId(T entidad, Long id) {
        ReflectionTestUtils.setField(entidad, "id", id);
        return entidad;
    }
}
