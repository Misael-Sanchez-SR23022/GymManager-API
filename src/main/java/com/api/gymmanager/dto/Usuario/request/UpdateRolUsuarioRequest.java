package com.api.gymmanager.dto.Usuario.request;

import jakarta.validation.constraints.NotNull;

public record UpdateRolUsuarioRequest(
    @NotNull Integer rolId
) {}
