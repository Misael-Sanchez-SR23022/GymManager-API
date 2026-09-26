package com.api.gymmanager.dto.Rol.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRolRequest(
    @NotBlank String nombreRol
) {}
