package com.pruebadev.franquicias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebadev.franquicias.entity.Franquicia;

public interface FranquiciaRepository extends JpaRepository<Franquicia, Long> {
    
    boolean existsByNombre(String nombre);
    
}
