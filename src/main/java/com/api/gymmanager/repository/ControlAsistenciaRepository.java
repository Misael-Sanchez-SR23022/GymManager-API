package com.api.gymmanager.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.gymmanager.model.ControlAsistencia;

@Repository
public interface ControlAsistenciaRepository extends JpaRepository<ControlAsistencia, Integer> {

    // Registros de asistencia de una inscripción
    List<ControlAsistencia> findByInscripcionInscripcionId(Integer inscripcionId);
        // Historial de un miembro: todos los registros de sus inscripciones
    List<ControlAsistencia> findByInscripcionUsuarioUsuarioIdOrderByFechaAsistenciaDesc(Integer usuarioId);

    boolean existsByInscripcionInscripcionIdAndFechaAsistencia(
            Integer inscripcionId, LocalDate fechaAsistencia);
}
