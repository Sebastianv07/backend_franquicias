package com.pruebadev.franquicias.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.SucursalResponse;
import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.repository.SucursalRepository;

@Service
@Transactional
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final BuscadorRecursos buscadorRecursos;

    public SucursalService(SucursalRepository sucursalRepository, BuscadorRecursos buscadorRecursos) {
        this.sucursalRepository = sucursalRepository;
        this.buscadorRecursos = buscadorRecursos;
    }

    public SucursalResponse agregar(Long franquiciaId, NombreRequest request) {
        Franquicia franquicia = buscadorRecursos.obtenerFranquicia(franquiciaId);
        validarNombreDisponible(request.nombre(), franquiciaId);
        return SucursalResponse.of(sucursalRepository.save(new Sucursal(request.nombre(), franquicia)));
    }

    public SucursalResponse renombrar(Long franquiciaId, Long sucursalId, NombreRequest request) {
        Sucursal sucursal = buscadorRecursos.obtenerSucursal(franquiciaId, sucursalId);
        if (!sucursal.getNombre().equals(request.nombre())) {
            validarNombreDisponible(request.nombre(), franquiciaId);
        }
        sucursal.setNombre(request.nombre());
        return SucursalResponse.of(sucursal);
    }

    private void validarNombreDisponible(String nombre, Long franquiciaId) {
        if (sucursalRepository.existsByNombreAndFranquiciaId(nombre, franquiciaId)) {
            throw new RecursoDuplicadoException(
                    "Ya existe una sucursal con el nombre: '" + nombre + "' en la franquicia con id: " + franquiciaId);
        }
    }
}
