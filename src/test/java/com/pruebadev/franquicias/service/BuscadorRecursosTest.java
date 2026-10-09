package com.pruebadev.franquicias.service;

import static com.pruebadev.franquicias.service.EntidadesPrueba.franquicia;
import static com.pruebadev.franquicias.service.EntidadesPrueba.producto;
import static com.pruebadev.franquicias.service.EntidadesPrueba.sucursal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Producto;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.repository.FranquiciaRepository;
import com.pruebadev.franquicias.repository.ProductoRepository;
import com.pruebadev.franquicias.repository.SucursalRepository;

@ExtendWith(MockitoExtension.class)
class BuscadorRecursosTest {

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private BuscadorRecursos buscadorRecursos;

    private Franquicia franquicia;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        franquicia = franquicia(1L, "Franquicia A");
        sucursal = sucursal(2L, "Centro", franquicia);
    }

    @Test
    void obtenerFranquiciaDevuelveLaFranquiciaExistente() {
        when(franquiciaRepository.findById(1L)).thenReturn(Optional.of(franquicia));

        assertThat(buscadorRecursos.obtenerFranquicia(1L)).isSameAs(franquicia);
    }

    @Test
    void obtenerFranquiciaFallaSiNoExiste() {
        when(franquiciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscadorRecursos.obtenerFranquicia(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No se encontró la franquicia con id: 99");
    }

    @Test
    void obtenerSucursalNoConsultaSucursalesSiLaFranquiciaNoExiste() {
        when(franquiciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscadorRecursos.obtenerSucursal(99L, 2L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(sucursalRepository);
    }

    @Test
    void obtenerSucursalFallaSiNoPerteneceALaFranquicia() {
        when(franquiciaRepository.findById(1L)).thenReturn(Optional.of(franquicia));
        when(sucursalRepository.findByIdAndFranquiciaId(8L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscadorRecursos.obtenerSucursal(1L, 8L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No se encontró la sucursal con id: 8 en la franquicia con id: 1");
    }

    @Test
    void obtenerProductoDevuelveElProductoDeLaSucursal() {
        Producto producto = producto(3L, "Café", 10, sucursal);
        when(franquiciaRepository.findById(1L)).thenReturn(Optional.of(franquicia));
        when(sucursalRepository.findByIdAndFranquiciaId(2L, 1L)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findByIdAndSucursalIdAndSucursalFranquiciaId(3L, 2L, 1L))
                .thenReturn(Optional.of(producto));

        assertThat(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).isSameAs(producto);
    }

    @Test
    void obtenerProductoFallaSiNoPerteneceALaSucursal() {
        when(franquiciaRepository.findById(1L)).thenReturn(Optional.of(franquicia));
        when(sucursalRepository.findByIdAndFranquiciaId(2L, 1L)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findByIdAndSucursalIdAndSucursalFranquiciaId(3L, 2L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscadorRecursos.obtenerProducto(1L, 2L, 3L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No se encontró el producto con id: 3 en la sucursal con id: 2");
    }
}
