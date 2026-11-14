package com.api.gymmanager.event;

import java.time.LocalDate;

public record InasistenciaRegistradaEvent(
    Integer usuarioId,
    String nombreMiembro,
    String correo,
    String nombreClase,
    LocalDate fechaAsistencia
) {}