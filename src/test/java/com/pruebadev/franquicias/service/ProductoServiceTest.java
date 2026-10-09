package com.pruebadev.franquicias.service;

import static com.pruebadev.franquicias.service.EntidadesPrueba.conId;
import static com.pruebadev.franquicias.service.EntidadesPrueba.franquicia;
import static com.pruebadev.franquicias.service.EntidadesPrueba.producto;
import static com.pruebadev.franquicias.service.EntidadesPrueba.sucursal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.ProductoMayorStockResponse;
import com.pruebadev.franquicias.dto.ProductoRequest;
import com.pruebadev.franquicias.dto.ProductoResponse;
import com.pruebadev.franquicias.dto.StockRequest;
import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Producto;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private BuscadorRecursos buscadorRecursos;

    @InjectMocks
    private ProductoService productoService;

    private Franquicia franquicia;
    private Sucursal sucursal;
    private Producto producto;

    @BeforeEach
    void setUp() {
        franquicia = franquicia(1L, "Franquicia A");
        sucursal = sucursal(2L, "Centro", franquicia);
        producto = producto(3L, "Café", 10, sucursal);
    }

    @Test
    void agregarGuardaElProductoEnLaSucursal() {
        when(buscadorRecursos.obtenerSucursal(1L, 2L)).thenReturn(sucursal);
        when(productoRepository.existsByNombreAndSucursalId("Té", 2L)).thenReturn(false);
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocacion -> conId(invocacion.getArgument(0), 9L));

        ProductoResponse respuesta = productoService.agregar(1L, 2L, new ProductoRequest("Té", 15));

        assertThat(respuesta).isEqualTo(new ProductoResponse(9L, "Té", 15, 2L));
    }

    @Test
    void agregarFallaSiLaSucursalNoExiste() {
        when(buscadorRecursos.obtenerSucursal(1L, 2L)).thenThrow(new RecursoNoEncontradoException("no existe"));

        assertThatThrownBy(() -> productoService.agregar(1L, 2L, new ProductoRequest("Té", 15)))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(productoRepository);
    }

    @Test
    void agregarFallaSiElNombreYaExisteEnLaSucursal() {
        when(buscadorRecursos.obtenerSucursal(1L, 2L)).thenReturn(sucursal);
        when(productoRepository.existsByNombreAndSucursalId("Café", 2L)).thenReturn(true);

        assertThatThrownBy(() -> productoService.agregar(1L, 2L, new ProductoRequest("Café", 5)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessage("Ya existe un producto con el nombre: 'Café' en la sucursal con id: 2");
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminarBorraElProductoEncontrado() {
        when(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).thenReturn(producto);

        productoService.eliminar(1L, 2L, 3L);

        verify(productoRepository).delete(producto);
    }

    @Test
    void eliminarNoBorraSiElProductoNoExiste() {
        when(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).thenThrow(new RecursoNoEncontradoException("no existe"));

        assertThatThrownBy(() -> productoService.eliminar(1L, 2L, 3L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(productoRepository);
    }

    @Test
    void actualizarStockCambiaElStock() {
        when(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).thenReturn(producto);

        ProductoResponse respuesta = productoService.actualizarStock(1L, 2L, 3L, new StockRequest(50));

        assertThat(respuesta).isEqualTo(new ProductoResponse(3L, "Café", 50, 2L));
        assertThat(producto.getStock()).isEqualTo(50);
    }

    @Test
    void renombrarActualizaElNombre() {
        when(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).thenReturn(producto);
        when(productoRepository.existsByNombreAndSucursalId("Café Premium", 2L)).thenReturn(false);

        ProductoResponse respuesta = productoService.renombrar(1L, 2L, 3L, new NombreRequest("Café Premium"));

        assertThat(respuesta.nombre()).isEqualTo("Café Premium");
    }

    @Test
    void renombrarConElMismoNombreNoValidaDuplicados() {
        when(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).thenReturn(producto);

        productoService.renombrar(1L, 2L, 3L, new NombreRequest("Café"));

        verify(productoRepository, never()).existsByNombreAndSucursalId(any(), anyLong());
    }

    @Test
    void renombrarFallaSiOtroProductoDeLaSucursalTieneElNombre() {
        when(buscadorRecursos.obtenerProducto(1L, 2L, 3L)).thenReturn(producto);
        when(productoRepository.existsByNombreAndSucursalId("Té", 2L)).thenReturn(true);

        assertThatThrownBy(() -> productoService.renombrar(1L, 2L, 3L, new NombreRequest("Té")))
                .isInstanceOf(RecursoDuplicadoException.class);
        assertThat(producto.getNombre()).isEqualTo("Café");
    }

    @Test
    void obtenerMayorStockDevuelveUnProductoPorSucursal() {
        Producto pan = producto(6L, "Pan", 50, sucursal(4L, "Norte", franquicia));
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(productoRepository.findProductosWithMaxStockByFranquiciaId(1L)).thenReturn(List.of(producto, pan));

        List<ProductoMayorStockResponse> respuesta = productoService.obtenerMayorStockPorSucursal(1L);

        assertThat(respuesta).containsExactly(
                new ProductoMayorStockResponse(2L, "Centro", 3L, "Café", 10),
                new ProductoMayorStockResponse(4L, "Norte", 6L, "Pan", 50));
    }

    @Test
    void obtenerMayorStockConEmpateConservaElPrimeroDeLaSucursal() {
        Producto te = producto(8L, "Té", 10, sucursal);
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(productoRepository.findProductosWithMaxStockByFranquiciaId(1L)).thenReturn(List.of(producto, te));

        List<ProductoMayorStockResponse> respuesta = productoService.obtenerMayorStockPorSucursal(1L);

        assertThat(respuesta).singleElement()
                .extracting(ProductoMayorStockResponse::productoId).isEqualTo(3L);
    }

    @Test
    void obtenerMayorStockSinProductosDevuelveListaVacia() {
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(productoRepository.findProductosWithMaxStockByFranquiciaId(1L)).thenReturn(List.of());

        assertThat(productoService.obtenerMayorStockPorSucursal(1L)).isEmpty();
    }

    @Test
    void obtenerMayorStockFallaSiLaFranquiciaNoExiste() {
        when(buscadorRecursos.obtenerFranquicia(99L)).thenThrow(new RecursoNoEncontradoException("no existe"));

        assertThatThrownBy(() -> productoService.obtenerMayorStockPorSucursal(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(productoRepository);
    }
}
