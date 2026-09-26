package com.api.gymmanager.dto.Rol.request;

import jakarta.validation.constraints.NotNull;

public record SearchRolRequest(
    @NotNull Integer rolId
) {}
