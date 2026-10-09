package com.pruebadev.franquicias.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.SucursalResponse;
import com.pruebadev.franquicias.service.SucursalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/franquicias/{franquiciaId}/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SucursalResponse agregar(@PathVariable Long franquiciaId, @RequestBody @Valid NombreRequest request) {
        return sucursalService.agregar(franquiciaId, request);
    }

    @PatchMapping("/{sucursalId}")
    public SucursalResponse renombrar(@PathVariable Long franquiciaId, @PathVariable Long sucursalId,
            @RequestBody @Valid NombreRequest request) {
        return sucursalService.renombrar(franquiciaId, sucursalId, request);
    }
}
