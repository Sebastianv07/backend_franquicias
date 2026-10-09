package com.pruebadev.franquicias.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequest(

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 150, message = "El nombre no puede tener más de 150 caracteres")
        String nombre,

        @NotNull(message = "El stock no puede estar vacío")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock) {

    public ProductoRequest {
        nombre = Normalizador.recortar(nombre);
    }
}
