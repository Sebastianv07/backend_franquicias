package com.pruebadev.franquicias.service;

import static com.pruebadev.franquicias.service.EntidadesPrueba.conId;
import static com.pruebadev.franquicias.service.EntidadesPrueba.franquicia;
import static com.pruebadev.franquicias.service.EntidadesPrueba.sucursal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.SucursalResponse;
import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.entity.Sucursal;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.repository.SucursalRepository;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private BuscadorRecursos buscadorRecursos;

    @InjectMocks
    private SucursalService sucursalService;

    private Franquicia franquicia;

    @BeforeEach
    void setUp() {
        franquicia = franquicia(1L, "Franquicia A");
    }

    @Test
    void agregarGuardaLaSucursalEnLaFranquicia() {
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(sucursalRepository.existsByNombreAndFranquiciaId("Centro", 1L)).thenReturn(false);
        when(sucursalRepository.save(any(Sucursal.class)))
                .thenAnswer(invocacion -> conId(invocacion.getArgument(0), 2L));

        SucursalResponse respuesta = sucursalService.agregar(1L, new NombreRequest("Centro"));

        assertThat(respuesta).isEqualTo(new SucursalResponse(2L, "Centro", 1L));
    }

    @Test
    void agregarFallaSiLaFranquiciaNoExiste() {
        when(buscadorRecursos.obtenerFranquicia(99L)).thenThrow(new RecursoNoEncontradoException("no existe"));

        assertThatThrownBy(() -> sucursalService.agregar(99L, new NombreRequest("Centro")))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(sucursalRepository);
    }

    @Test
    void agregarFallaSiElNombreYaExisteEnLaFranquicia() {
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(sucursalRepository.existsByNombreAndFranquiciaId("Centro", 1L)).thenReturn(true);

        assertThatThrownBy(() -> sucursalService.agregar(1L, new NombreRequest("Centro")))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessage("Ya existe una sucursal con el nombre: 'Centro' en la franquicia con id: 1");
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void renombrarActualizaElNombre() {
        Sucursal sucursal = sucursal(2L, "Centro", franquicia);
        when(buscadorRecursos.obtenerSucursal(1L, 2L)).thenReturn(sucursal);
        when(sucursalRepository.existsByNombreAndFranquiciaId("Norte", 1L)).thenReturn(false);

        SucursalResponse respuesta = sucursalService.renombrar(1L, 2L, new NombreRequest("Norte"));

        assertThat(respuesta).isEqualTo(new SucursalResponse(2L, "Norte", 1L));
        assertThat(sucursal.getNombre()).isEqualTo("Norte");
    }

    @Test
    void renombrarConElMismoNombreNoValidaDuplicados() {
        when(buscadorRecursos.obtenerSucursal(1L, 2L)).thenReturn(sucursal(2L, "Centro", franquicia));

        sucursalService.renombrar(1L, 2L, new NombreRequest("Centro"));

        verify(sucursalRepository, never()).existsByNombreAndFranquiciaId(any(), anyLong());
    }

    @Test
    void renombrarFallaSiOtraSucursalDeLaFranquiciaTieneElNombre() {
        Sucursal sucursal = sucursal(2L, "Centro", franquicia);
        when(buscadorRecursos.obtenerSucursal(1L, 2L)).thenReturn(sucursal);
        when(sucursalRepository.existsByNombreAndFranquiciaId("Norte", 1L)).thenReturn(true);

        assertThatThrownBy(() -> sucursalService.renombrar(1L, 2L, new NombreRequest("Norte")))
                .isInstanceOf(RecursoDuplicadoException.class);
        assertThat(sucursal.getNombre()).isEqualTo("Centro");
    }
}
