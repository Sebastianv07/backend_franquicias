package com.pruebadev.franquicias.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.repository.FranquiciaRepository;

@Service
@Transactional
public class FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;
    private final BuscadorRecursos buscadorRecursos;

    public FranquiciaService(FranquiciaRepository franquiciaRepository, BuscadorRecursos buscadorRecursos) {
        this.franquiciaRepository = franquiciaRepository;
        this.buscadorRecursos = buscadorRecursos;
    }

    public FranquiciaResponse crear(NombreRequest request) {
        validarNombreDisponible(request.nombre());
        return FranquiciaResponse.of(franquiciaRepository.save(new Franquicia(request.nombre())));
    }

    public FranquiciaResponse renombrar(Long franquiciaId, NombreRequest request) {
        Franquicia franquicia = buscadorRecursos.obtenerFranquicia(franquiciaId);
        if (!franquicia.getNombre().equals(request.nombre())) {
            validarNombreDisponible(request.nombre());
        }
        franquicia.setNombre(request.nombre());
        return FranquiciaResponse.of(franquicia);
    }

    private void validarNombreDisponible(String nombre) {
        if (franquiciaRepository.existsByNombre(nombre)) {
            throw new RecursoDuplicadoException("Ya existe una franquicia con el nombre: '" + nombre + "'");
        }
    }
}
