package com.api.gymmanager.dto.ControlAsistencia.response;

import java.util.List;

public record HistorialClaseResponse(
    Integer claseId,
    String nombreClase,
    long totalAsistio,
    long totalFalto,
    List<RegistroAsistenciaResponse> registros
) {}
