package com.pruebadev.franquicias.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
import com.pruebadev.franquicias.dto.SucursalResponse;
import com.pruebadev.franquicias.service.SucursalService;

@WebMvcTest(SucursalController.class)
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SucursalService sucursalService;

    @Test
    void agregarDevuelve201() throws Exception {
        when(sucursalService.agregar(1L, new NombreRequest("Centro")))
                .thenReturn(new SucursalResponse(2L, "Centro", 1L));

        mockMvc.perform(post("/api/franquicias/1/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Centro"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("Centro"))
                .andExpect(jsonPath("$.franquiciaId").value(1));
    }

    @Test
    void agregarConNombreMuyLargoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/franquicias/1/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"" + "a".repeat(151) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value("El nombre no puede tener más de 150 caracteres"));
        verifyNoInteractions(sucursalService);
    }

    @Test
    void renombrarDevuelve200() throws Exception {
        when(sucursalService.renombrar(1L, 2L, new NombreRequest("Norte")))
                .thenReturn(new SucursalResponse(2L, "Norte", 1L));

        mockMvc.perform(patch("/api/franquicias/1/sucursales/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Norte"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Norte"));
    }
}
