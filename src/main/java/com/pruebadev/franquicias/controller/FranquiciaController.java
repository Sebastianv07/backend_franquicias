package com.pruebadev.franquicias.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.Nombre;
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
}
