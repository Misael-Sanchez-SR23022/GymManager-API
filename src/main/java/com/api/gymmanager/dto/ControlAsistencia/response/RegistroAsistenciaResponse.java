package com.api.gymmanager.dto.ControlAsistencia.response;

import java.time.LocalDate;

import com.api.gymmanager.model.EnumModel.AsistenciaEnum;

public record RegistroAsistenciaResponse(
    Integer asistenciaId,
    LocalDate fechaAsistencia,
    AsistenciaEnum asistencia
) {}