package com.api.gymmanager.dto.ControlAsistencia.response;

import java.time.LocalDate;

import com.api.gymmanager.model.EnumModel.AsistenciaEnum;

public record ControlAsistenciaResponse(
    Integer asistenciaId,
    Integer inscripcionId,
    Integer claseId,
    String nombreClase,
    Integer usuarioId,
    AsistenciaEnum asistencia,
    LocalDate fechaAsistencia
) {}