package com.api.gymmanager.dto.ControlAsistencia.request;

import com.api.gymmanager.model.EnumModel.AsistenciaEnum;

import jakarta.validation.constraints.NotNull;

public record AsistenciaItemRequest(
    @NotNull Integer usuarioId,
    @NotNull AsistenciaEnum asistencia
) {}