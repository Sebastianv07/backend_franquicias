package com.pruebadev.franquicias.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.Nombre;
import com.pruebadev.franquicias.dto.ProductoMayorStockResponse;
import com.pruebadev.franquicias.dto.ProductoResponse;
import com.pruebadev.franquicias.dto.Stock;
import com.pruebadev.franquicias.dto.SucursalResponse;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.entity.Producto;
import com.pruebadev.franquicias.repository.FranquiciaRepository;
import com.pruebadev.franquicias.repository.ProductoRepository;
import com.pruebadev.franquicias.repository.SucursalRepository;

@Service
@Transactional
public class FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;

    public FranquiciaService(FranquiciaRepository franquiciaRepository, SucursalRepository sucursalRepository, ProductoRepository productoRepository) {
        this.franquiciaRepository = franquiciaRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    public FranquiciaResponse crearFranquicia(Nombre request) {
        String nombre = request.nombre().trim();
        if (franquiciaRepository.existsByNombre(nombre)) {
            throw new RecursoDuplicadoException("Ya existe una franquicia con el nombre: '" + nombre + "'");
        }
        return FranquiciaResponse.of(franquiciaRepository.save(new Franquicia(nombre)));
    }

    public FranquiciaResponse renombrarFranquicia(Long franquiciaId, Nombre request) {
        Franquicia franquicia = obtenerFranquicia(franquiciaId);
        String nombre = request.nombre().trim();
        if (!franquicia.getNombre().equals(nombre) && franquiciaRepository.existsByNombre(nombre)) {
            throw new RecursoDuplicadoException("Ya existe una franquicia con el nombre: '" + nombre + "'");
        }
        franquicia.setNombre(nombre);
        return FranquiciaResponse.of(franquicia);
    }

    private Franquicia obtenerFranquicia(Long id) {
        return franquiciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la franquicia con id: " + id));
    }

    public SucursalResponse agregarSucursal(Long franquiciaId, Nombre request) {
        Franquicia franquicia = obtenerFranquicia(franquiciaId);
        String nombre = request.nombre().trim();
        if (sucursalRepository.existsByNombreAndFranquiciaId(nombre, franquiciaId)) {
            throw new RecursoDuplicadoException("Ya existe una sucursal con el nombre: '" + nombre + "' en la franquicia con id: " + franquiciaId);
        }
        return SucursalResponse.of(sucursalRepository.save(new Sucursal(nombre, franquicia)));
    }

    public SucursalResponse renombrarSucursal(Long franquiciaId, Long sucursalId, Nombre request) {
        Sucursal sucursal = obtenerSucursal(franquiciaId, sucursalId);
        String nombre = request.nombre().trim();
        if (!sucursal.getNombre().equals(nombre) && sucursalRepository.existsByNombreAndFranquiciaId(nombre, franquiciaId)) {
            throw new RecursoDuplicadoException("Ya existe una sucursal con el nombre: '" + nombre + "' en la franquicia con id: " + franquiciaId);
        }
        sucursal.setNombre(nombre);
        return SucursalResponse.of(sucursal);
    }

    private Sucursal obtenerSucursal(Long franquiciaId, Long sucursalId) {
        obtenerFranquicia(franquiciaId);
        return sucursalRepository.findByIdAndFranquiciaId(sucursalId, franquiciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la sucursal con id: " + sucursalId + " en la franquicia con id: " + franquiciaId));
    }

    public ProductoResponse agregarProducto(Long franquiciaId, Long sucursalId, com.pruebadev.franquicias.dto.Producto request) {
        Sucursal sucursal = obtenerSucursal(franquiciaId, sucursalId);
        String nombre = request.nombre().trim();
        if (productoRepository.existsByNombreAndSucursalId(nombre, sucursalId)) {
            throw new RecursoDuplicadoException("Ya existe un producto con el nombre: '" + nombre + "' en la sucursal con id: " + sucursalId);
        }
        return ProductoResponse.of(productoRepository.save(new Producto(nombre, request.stock(), sucursal)));
    }

    public void eliminarProducto(Long franquiciaId, Long sucursalId, Long productoId) {
        productoRepository.delete(obtenerProducto(franquiciaId, sucursalId, productoId));
    }

    private Producto obtenerProducto(Long franquiciaId, Long sucursalId, Long productoId) {
        obtenerSucursal(franquiciaId, sucursalId);
        return productoRepository.findByIdAndSucursalIdAndSucursalFranquiciaId(productoId, sucursalId, franquiciaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto con id: " + productoId + " en la sucursal con id: " + sucursalId));
    }

    public ProductoResponse actualizarStock(Long franquiciaId, Long sucursalId, Long productoId, Stock request) {
        Producto producto = obtenerProducto(franquiciaId, sucursalId, productoId);
        producto.setStock(request.stock());
        return ProductoResponse.of(producto);
    }

    public ProductoResponse renombrarProducto(Long franquiciaId, Long sucursalId, Long productoId, Nombre request) {
        Producto producto = obtenerProducto(franquiciaId, sucursalId, productoId);
        String nombre = request.nombre().trim();
        if (!producto.getNombre().equals(nombre) && productoRepository.existsByNombreAndSucursalId(nombre, sucursalId)) {
            throw new RecursoDuplicadoException("Ya existe un producto con el nombre: '" + nombre + "' en la sucursal con id: " + sucursalId);
        }
        producto.setNombre(nombre);
        return ProductoResponse.of(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoMayorStockResponse> obtenerProductosConMayorStockSucursal(Long franquiciaId) {
        obtenerFranquicia(franquiciaId);
        Map<Long, Producto> mayorPorSucursal = new LinkedHashMap<>();
        for (Producto producto : productoRepository.findProductosWithMaxStockByFranquiciaId(franquiciaId)) {
            mayorPorSucursal.putIfAbsent(producto.getSucursal().getId(), producto);
        }
        return mayorPorSucursal.values().stream().map(ProductoMayorStockResponse::of).toList();
    }
}