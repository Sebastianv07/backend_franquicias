package com.pruebadev.franquicias.service;

import static com.pruebadev.franquicias.service.EntidadesPrueba.conId;
import static com.pruebadev.franquicias.service.EntidadesPrueba.franquicia;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.entity.Franquicia;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.repository.FranquiciaRepository;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceTest {

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @Mock
    private BuscadorRecursos buscadorRecursos;

    @InjectMocks
    private FranquiciaService franquiciaService;

    @Test
    void crearGuardaLaFranquicia() {
        when(franquiciaRepository.existsByNombre("Franquicia A")).thenReturn(false);
        when(franquiciaRepository.save(any(Franquicia.class)))
                .thenAnswer(invocacion -> conId(invocacion.getArgument(0), 1L));

        FranquiciaResponse respuesta = franquiciaService.crear(new NombreRequest("Franquicia A"));

        assertThat(respuesta).isEqualTo(new FranquiciaResponse(1L, "Franquicia A"));
    }

    @Test
    void crearFallaSiElNombreYaExiste() {
        when(franquiciaRepository.existsByNombre("Franquicia A")).thenReturn(true);

        assertThatThrownBy(() -> franquiciaService.crear(new NombreRequest("Franquicia A")))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessage("Ya existe una franquicia con el nombre: 'Franquicia A'");
        verify(franquiciaRepository, never()).save(any());
    }

    @Test
    void renombrarActualizaElNombre() {
        Franquicia franquicia = franquicia(1L, "Antigua");
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(franquiciaRepository.existsByNombre("Nueva")).thenReturn(false);

        FranquiciaResponse respuesta = franquiciaService.renombrar(1L, new NombreRequest("Nueva"));

        assertThat(respuesta).isEqualTo(new FranquiciaResponse(1L, "Nueva"));
        assertThat(franquicia.getNombre()).isEqualTo("Nueva");
    }

    @Test
    void renombrarConElMismoNombreNoValidaDuplicados() {
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia(1L, "Franquicia A"));

        franquiciaService.renombrar(1L, new NombreRequest("Franquicia A"));

        verify(franquiciaRepository, never()).existsByNombre(any());
    }

    @Test
    void renombrarFallaSiOtraFranquiciaTieneElNombre() {
        Franquicia franquicia = franquicia(1L, "Antigua");
        when(buscadorRecursos.obtenerFranquicia(1L)).thenReturn(franquicia);
        when(franquiciaRepository.existsByNombre("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> franquiciaService.renombrar(1L, new NombreRequest("Ocupado")))
                .isInstanceOf(RecursoDuplicadoException.class);
        assertThat(franquicia.getNombre()).isEqualTo("Antigua");
    }

    @Test
    void renombrarFallaSiLaFranquiciaNoExiste() {
        when(buscadorRecursos.obtenerFranquicia(99L))
                .thenThrow(new RecursoNoEncontradoException("No se encontró la franquicia con id: 99"));

        assertThatThrownBy(() -> franquiciaService.renombrar(99L, new NombreRequest("Nueva")))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
