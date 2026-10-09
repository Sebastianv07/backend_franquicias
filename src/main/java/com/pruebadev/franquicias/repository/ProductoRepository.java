package com.pruebadev.franquicias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pruebadev.franquicias.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByIdAndSucursalIdAndSucursalFranquiciaId(Long id, Long sucursalId, Long franquiciaId);

    boolean existsByNombreAndSucursalId(String nombre, Long sucursalId);

    @Query("""
            SELECT p FROM Producto p JOIN FETCH p.sucursal s
            WHERE s.franquicia.id = :franquiciaId
              AND p.stock = (SELECT MAX(p2.stock) FROM Producto p2 WHERE p2.sucursal = p.sucursal)
            ORDER BY s.id, p.id
            """)
    List<Producto> findProductosWithMaxStockByFranquiciaId(@Param("franquiciaId") Long franquiciaId);

}
