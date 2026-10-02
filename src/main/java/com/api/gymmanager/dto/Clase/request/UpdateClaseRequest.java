package com.api.gymmanager.dto.Clase.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClaseRequest(
    @NotBlank @Size(max = 50) String nombreClase,
    @NotNull @Min(1) Integer capacidadClase,
    @NotNull Integer entrenadorId
) {}