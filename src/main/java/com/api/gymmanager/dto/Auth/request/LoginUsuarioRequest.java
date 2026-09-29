package com.api.gymmanager.dto.Auth.request;

import jakarta.validation.constraints.NotBlank;

public record LoginUsuarioRequest(
    @NotBlank String nombreUsuario,
    @NotBlank String contrasenia
) {}