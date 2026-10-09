package com.pruebadev.franquicias.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pruebadev.franquicias.dto.NombreRequest;
import com.pruebadev.franquicias.dto.ProductoRequest;
import com.pruebadev.franquicias.dto.ProductoResponse;
import com.pruebadev.franquicias.dto.StockRequest;
import com.pruebadev.franquicias.service.ProductoService;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    private static final String URL_PRODUCTOS = "/api/franquicias/1/sucursales/2/productos";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void agregarDevuelve201() throws Exception {
        when(productoService.agregar(1L, 2L, new ProductoRequest("Café", 10)))
                .thenReturn(new ProductoResponse(3L, "Café", 10, 2L));

        mockMvc.perform(post(URL_PRODUCTOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Café", "stock": 10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.sucursalId").value(2));
    }

    @Test
    void agregarConDatosInvalidosDevuelve400ConCadaError() throws Exception {
        mockMvc.perform(post(URL_PRODUCTOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "stock": -1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value("El nombre no puede estar vacío"))
                .andExpect(jsonPath("$.errores.stock").value("El stock no puede ser negativo"));
        verifyNoInteractions(productoService);
    }

    @Test
    void eliminarDevuelve204() throws Exception {
        mockMvc.perform(delete(URL_PRODUCTOS + "/3"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L, 2L, 3L);
    }

    @Test
    void actualizarStockDevuelve200() throws Exception {
        when(productoService.actualizarStock(1L, 2L, 3L, new StockRequest(50)))
                .thenReturn(new ProductoResponse(3L, "Café", 50, 2L));

        mockMvc.perform(patch(URL_PRODUCTOS + "/3/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stock": 50}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    void actualizarStockSinValorDevuelve400() throws Exception {
        mockMvc.perform(patch(URL_PRODUCTOS + "/3/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.stock").value("El stock no puede estar vacío"));
    }

    @Test
    void renombrarDevuelve200() throws Exception {
        when(productoService.renombrar(1L, 2L, 3L, new NombreRequest("Té")))
                .thenReturn(new ProductoResponse(3L, "Té", 10, 2L));

        mockMvc.perform(patch(URL_PRODUCTOS + "/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Té"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Té"));
    }
}
