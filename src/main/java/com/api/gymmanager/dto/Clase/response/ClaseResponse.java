package com.api.gymmanager.dto.Clase.response;

public record ClaseResponse(
    Integer claseId,
    String nombreClase,
    Integer capacidadClase,
    Integer entrenadorId,
    String nombreEntrenador
) {}