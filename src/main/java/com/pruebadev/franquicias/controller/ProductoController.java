package com.pruebadev.franquicias.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.ProductoRequest;
import com.pruebadev.franquicias.dto.ProductoResponse;
import com.pruebadev.franquicias.dto.StockRequest;
import com.pruebadev.franquicias.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse agregar(@PathVariable Long franquiciaId, @PathVariable Long sucursalId,
            @RequestBody @Valid ProductoRequest request) {
        return productoService.agregar(franquiciaId, sucursalId, request);
    }

    @DeleteMapping("/{productoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long franquiciaId, @PathVariable Long sucursalId,
            @PathVariable Long productoId) {
        productoService.eliminar(franquiciaId, sucursalId, productoId);
    }

    @PatchMapping("/{productoId}/stock")
    public ProductoResponse actualizarStock(@PathVariable Long franquiciaId, @PathVariable Long sucursalId,
            @PathVariable Long productoId, @RequestBody @Valid StockRequest request) {
        return productoService.actualizarStock(franquiciaId, sucursalId, productoId, request);
    }

    @PatchMapping("/{productoId}")
    public ProductoResponse renombrar(@PathVariable Long franquiciaId, @PathVariable Long sucursalId,
            @PathVariable Long productoId, @RequestBody @Valid NombreRequest request) {
        return productoService.renombrar(franquiciaId, sucursalId, productoId, request);
    }
}
