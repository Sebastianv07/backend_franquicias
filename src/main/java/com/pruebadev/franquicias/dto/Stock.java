package com.pruebadev.franquicias.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Stock(

    @NotNull (message = "El stock no puede estar vacío")
    @Min (value = 0, message = "El stock no puede ser negativo")
    Integer stock) {    
}
