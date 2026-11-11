package com.api.gymmanager.dto.Inscripcion.response;


import java.time.LocalDateTime;

import com.api.gymmanager.model.EnumModel.EstadoEnum;

public record CreateInscripcionResponse(
    Integer inscripcionId,
    Integer claseId,
    String nombreClase,
    Integer usuarioId,
    LocalDateTime fechaRegistro,
    EstadoEnum estado
) {}