package com.api.gymmanager.dto.ControlAsistencia.response;

public record InscritoResponse(
    Integer inscripcionId,
    Integer usuarioId,
    String nombreMiembro
) {}