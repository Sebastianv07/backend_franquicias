package com.pruebadev.franquicias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.Nombre;
import com.pruebadev.franquicias.dto.Producto;
import com.pruebadev.franquicias.dto.ProductoMayorStockResponse;
import com.pruebadev.franquicias.dto.ProductoResponse;
import com.pruebadev.franquicias.dto.Stock;
import com.pruebadev.franquicias.dto.SucursalResponse;
import com.pruebadev.franquicias.service.FranquiciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping ("/api/franquicias")
public class FranquiciaController {
    
    private final FranquiciaService franquiciaService;

    public FranquiciaController(FranquiciaService franquiciaService) {
        this.franquiciaService = franquiciaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FranquiciaResponse crearFranquicia(@RequestBody @Valid Nombre request) {
        return franquiciaService.crearFranquicia(request);
    }

    @PatchMapping("/{franquiciaId}")
    public FranquiciaResponse renombrarFranquicia(@PathVariable Long franquiciaId, @RequestBody @Valid Nombre request) {
        return franquiciaService.renombrarFranquicia(franquiciaId, request);
    }

    @PostMapping("/{franquiciaId}/sucursales")
    @ResponseStatus (HttpStatus.CREATED)
    public SucursalResponse agregarSucursal(@PathVariable Long franquiciaId, @RequestBody @Valid Nombre request) {
        return franquiciaService.agregarSucursal(franquiciaId, request);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}")
    public SucursalResponse renombrarSucursal(@PathVariable Long franquiciaId, @PathVariable Long sucursalId, @RequestBody @Valid Nombre request) {
        return franquiciaService.renombrarSucursal(franquiciaId, sucursalId, request);
    }

    @PostMapping("/{franquiciaId}/sucursales/{sucursalId}/productos")
    @ResponseStatus (HttpStatus.CREATED)
    public ProductoResponse agregarProducto(@PathVariable Long franquiciaId, @PathVariable Long sucursalId, @RequestBody @Valid Producto request) {
        return franquiciaService.agregarProducto(franquiciaId, sucursalId, request);
    }

    @DeleteMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long franquiciaId, @PathVariable Long sucursalId, @PathVariable Long productoId) {
        franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock")
    public ProductoResponse actualizarStock(@PathVariable Long franquiciaId, @PathVariable Long sucursalId, @PathVariable Long productoId, @RequestBody @Valid Stock request) {
        return franquiciaService.actualizarStock(franquiciaId, sucursalId, productoId, request);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}")
    public ProductoResponse renombrarProducto(@PathVariable Long franquiciaId, @PathVariable Long sucursalId, @PathVariable Long productoId, @RequestBody @Valid Nombre request) {
        return franquiciaService.renombrarProducto(franquiciaId, sucursalId, productoId, request);
    }

    @GetMapping("/{franquiciaId}/productos/mayor-stock")
    public List<ProductoMayorStockResponse> obtenerProductosConMayorStockSucursal(@PathVariable Long franquiciaId) {
        return franquiciaService.obtenerProductosConMayorStockSucursal(franquiciaId);
    }
    
}
