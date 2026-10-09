package com.pruebadev.franquicias.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.ProductoMayorStockResponse;
import com.pruebadev.franquicias.dto.ProductoRequest;
import com.pruebadev.franquicias.dto.ProductoResponse;
import com.pruebadev.franquicias.dto.StockRequest;
import com.pruebadev.franquicias.entity.Producto;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.repository.ProductoRepository;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final BuscadorRecursos buscadorRecursos;

    public ProductoService(ProductoRepository productoRepository, BuscadorRecursos buscadorRecursos) {
        this.productoRepository = productoRepository;
        this.buscadorRecursos = buscadorRecursos;
    }

    public ProductoResponse agregar(Long franquiciaId, Long sucursalId, ProductoRequest request) {
        Sucursal sucursal = buscadorRecursos.obtenerSucursal(franquiciaId, sucursalId);
        validarNombreDisponible(request.nombre(), sucursalId);
        Producto producto = new Producto(request.nombre(), request.stock(), sucursal);
        return ProductoResponse.of(productoRepository.save(producto));
    }

    public void eliminar(Long franquiciaId, Long sucursalId, Long productoId) {
        productoRepository.delete(buscadorRecursos.obtenerProducto(franquiciaId, sucursalId, productoId));
    }

    public ProductoResponse actualizarStock(Long franquiciaId, Long sucursalId, Long productoId,
            StockRequest request) {
        Producto producto = buscadorRecursos.obtenerProducto(franquiciaId, sucursalId, productoId);
        producto.setStock(request.stock());
        return ProductoResponse.of(producto);
    }

    public ProductoResponse renombrar(Long franquiciaId, Long sucursalId, Long productoId, NombreRequest request) {
        Producto producto = buscadorRecursos.obtenerProducto(franquiciaId, sucursalId, productoId);
        if (!producto.getNombre().equals(request.nombre())) {
            validarNombreDisponible(request.nombre(), sucursalId);
        }
        producto.setNombre(request.nombre());
        return ProductoResponse.of(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoMayorStockResponse> obtenerMayorStockPorSucursal(Long franquiciaId) {
        buscadorRecursos.obtenerFranquicia(franquiciaId);
        Map<Long, Producto> mayorPorSucursal = new LinkedHashMap<>();
        for (Producto producto : productoRepository.findProductosWithMaxStockByFranquiciaId(franquiciaId)) {
            mayorPorSucursal.putIfAbsent(producto.getSucursal().getId(), producto);
        }
        return mayorPorSucursal.values().stream().map(ProductoMayorStockResponse::of).toList();
    }

    private void validarNombreDisponible(String nombre, Long sucursalId) {
        if (productoRepository.existsByNombreAndSucursalId(nombre, sucursalId)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un producto con el nombre: '" + nombre + "' en la sucursal con id: " + sucursalId);
        }
    }
}
