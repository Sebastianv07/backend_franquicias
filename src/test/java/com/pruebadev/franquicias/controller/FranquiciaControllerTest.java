package com.pruebadev.franquicias.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pruebadev.franquicias.dto.FranquiciaResponse;
import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.ProductoMayorStockResponse;
import com.pruebadev.franquicias.exception.RecursoDuplicadoException;
import com.pruebadev.franquicias.exception.RecursoNoEncontradoException;
import com.pruebadev.franquicias.service.FranquiciaService;
import com.pruebadev.franquicias.service.ProductoService;

@WebMvcTest(FranquiciaController.class)
class FranquiciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FranquiciaService franquiciaService;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void crearDevuelve201ConElNombreSinEspacios() throws Exception {
        when(franquiciaService.crear(new NombreRequest("Franquicia A")))
                .thenReturn(new FranquiciaResponse(1L, "Franquicia A"));

        mockMvc.perform(post("/api/franquicias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "  Franquicia A  "}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Franquicia A"));
    }

    @Test
    void crearConNombreVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/franquicias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value("El nombre no puede estar vacío"));
        verifyNoInteractions(franquiciaService);
    }

    @Test
    void crearSinCuerpoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/franquicias").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El cuerpo de la solicitud es inválido o está vacío"));
    }

    @Test
    void crearDuplicadaDevuelve409() throws Exception {
        when(franquiciaService.crear(any()))
                .thenThrow(new RecursoDuplicadoException("Ya existe una franquicia con el nombre: 'Franquicia A'"));

        mockMvc.perform(post("/api/franquicias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Franquicia A"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Ya existe una franquicia con el nombre: 'Franquicia A'"));
    }

    @Test
    void renombrarDevuelve200() throws Exception {
        when(franquiciaService.renombrar(1L, new NombreRequest("Nueva")))
                .thenReturn(new FranquiciaResponse(1L, "Nueva"));

        mockMvc.perform(patch("/api/franquicias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Nueva"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nueva"));
    }

    @Test
    void renombrarInexistenteDevuelve404() throws Exception {
        when(franquiciaService.renombrar(eq(99L), any()))
                .thenThrow(new RecursoNoEncontradoException("No se encontró la franquicia con id: 99"));

        mockMvc.perform(patch("/api/franquicias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Nueva"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No se encontró la franquicia con id: 99"));
    }

    @Test
    void idNoNumericoDevuelve400() throws Exception {
        mockMvc.perform(get("/api/franquicias/abc/productos/mayor-stock"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El valor 'abc' no es válido para franquiciaId"));
    }

    @Test
    void obtenerProductosConMayorStockDevuelveUnoPorSucursal() throws Exception {
        when(productoService.obtenerMayorStockPorSucursal(1L)).thenReturn(List.of(
                new ProductoMayorStockResponse(2L, "Centro", 3L, "Café", 30),
                new ProductoMayorStockResponse(4L, "Norte", 6L, "Pan", 50)));

        mockMvc.perform(get("/api/franquicias/1/productos/mayor-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sucursalNombre").value("Centro"))
                .andExpect(jsonPath("$[1].productoNombre").value("Pan"))
                .andExpect(jsonPath("$[1].stock").value(50));
    }
}
