package com.api.gymmanager.repository;

import java.sql.Time;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.gymmanager.model.HorarioClase;
import com.api.gymmanager.model.EnumModel.SemanaEnum;

public interface HorarioClaseRepository extends JpaRepository<HorarioClase, Integer> {

    boolean existsByClase_ClaseIdAndDiasClaseAndHoraInicioLessThanAndHoraFinGreaterThan(
            Integer claseId,
            SemanaEnum diasClase,
            Time horaFin,
            Time horaInicio
    );
}
