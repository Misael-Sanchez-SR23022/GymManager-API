package com.api.gymmanager.event;

import java.time.LocalDate;

public record InasistenciaRegistradaEvent(
    Integer usuarioId,
    String nombreUsuario,
    String correoUsuario,
    String nombreClase,
    LocalDate fechaAsistencia
) {}