package com.api.gymmanager.dto.Rol.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRolRequest(
    @NotBlank String nombreRol
) {}