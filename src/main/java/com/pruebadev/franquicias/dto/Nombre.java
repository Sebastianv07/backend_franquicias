package com.pruebadev.franquicias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Nombre(
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size (max = 150, message = "El nombre no puede tener más de 150 caracteres")
    String nombre ) {
}

