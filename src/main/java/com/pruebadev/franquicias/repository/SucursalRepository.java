package com.pruebadev.franquicias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pruebadev.franquicias.entity.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    
    Optional<Sucursal> findByIdAndFranquiciaId(Long id, Long franquiciaId);

    boolean existsByNombreAndFranquiciaId(String nombre, Long franquiciaId);
    
}
