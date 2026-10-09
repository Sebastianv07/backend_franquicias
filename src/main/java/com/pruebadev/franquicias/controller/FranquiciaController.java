package com.pruebadev.franquicias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.ProductoMayorStockResponse;
import com.pruebadev.franquicias.service.FranquiciaService;
import com.pruebadev.franquicias.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/franquicias")
public class FranquiciaController {

    private final FranquiciaService franquiciaService;
    private final ProductoService productoService;

    public FranquiciaController(FranquiciaService franquiciaService, ProductoService productoService) {
        this.franquiciaService = franquiciaService;
        this.productoService = productoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FranquiciaResponse crear(@RequestBody @Valid NombreRequest request) {
        return franquiciaService.crear(request);
    }

    @PatchMapping("/{franquiciaId}")
    public FranquiciaResponse renombrar(@PathVariable Long franquiciaId, @RequestBody @Valid NombreRequest request) {
        return franquiciaService.renombrar(franquiciaId, request);
    }

    @GetMapping("/{franquiciaId}/productos/mayor-stock")
    public List<ProductoMayorStockResponse> obtenerProductosConMayorStock(@PathVariable Long franquiciaId) {
        return productoService.obtenerMayorStockPorSucursal(franquiciaId);
    }
}
