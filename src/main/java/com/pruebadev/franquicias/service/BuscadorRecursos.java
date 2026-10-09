package com.pruebadev.franquicias.service;

import org.springframework.stereotype.Component;

import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Producto;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.repository.FranquiciaRepository;
import com.pruebadev.franquicias.repository.ProductoRepository;
import com.pruebadev.franquicias.repository.SucursalRepository;

@Component
public class BuscadorRecursos {

    private final FranquiciaRepository franquiciaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;

    public BuscadorRecursos(FranquiciaRepository franquiciaRepository, SucursalRepository sucursalRepository,
            ProductoRepository productoRepository) {
        this.franquiciaRepository = franquiciaRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    public Franquicia obtenerFranquicia(Long franquiciaId) {
        return franquiciaRepository.findById(franquiciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la franquicia con id: " + franquiciaId));
    }

    public Sucursal obtenerSucursal(Long franquiciaId, Long sucursalId) {
        obtenerFranquicia(franquiciaId);
        return sucursalRepository.findByIdAndFranquiciaId(sucursalId, franquiciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la sucursal con id: " + sucursalId + " en la franquicia con id: " + franquiciaId));
    }

    public Producto obtenerProducto(Long franquiciaId, Long sucursalId, Long productoId) {
        obtenerSucursal(franquiciaId, sucursalId);
        return productoRepository.findByIdAndSucursalIdAndSucursalFranquiciaId(productoId, sucursalId, franquiciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el producto con id: " + productoId + " en la sucursal con id: " + sucursalId));
    }
}
