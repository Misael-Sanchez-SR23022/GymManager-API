package com.api.gymmanager.dto.HorarioClases.request;

import java.sql.Time;

import com.api.gymmanager.model.EnumModel.SemanaEnum;

import jakarta.validation.constraints.NotNull;

public record CreateHorarioClaseRequest(
    @NotNull SemanaEnum diasClase,
    @NotNull Integer claseId,
    @NotNull Time horaInicio,
    @NotNull Time horaFin
){}
