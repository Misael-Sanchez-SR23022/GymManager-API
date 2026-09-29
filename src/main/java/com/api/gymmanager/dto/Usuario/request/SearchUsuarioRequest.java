package com.api.gymmanager.dto.Usuario.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SearchUsuarioRequest(
    @NotNull Integer usuarioId,
    @NotBlank String nombreUsuario
) {}
