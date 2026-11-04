package com.api.gymmanager.dto.HorarioClases.response;

import java.sql.Time;

import com.api.gymmanager.model.EnumModel.SemanaEnum;

public record CreateHorarioClaseResponse(
    Integer horarioId,
    SemanaEnum diasClase,
    Integer claseId,
    Time horaInicio,
    Time horaFin
){}
