package com.api.gymmanager.dto.ControlAsistencia.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateControlAsistenciaRequest(
    @NotNull Integer claseId,
    @NotNull LocalDate fechaAsistencia,
    @NotEmpty List<@Valid AsistenciaItemRequest> asistencias
) {}