package com.api.gymmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.gymmanager.model.EnumModel.EstadoEnum;
import com.api.gymmanager.model.Inscripcion;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    // Validar Cupo: cuenta las inscripciones de una clase según su estado
    long countByClaseClaseIdAndEstado(Integer claseId, EstadoEnum estado);

    // Inscribirse (flujo alterno): ¿el usuario ya está inscrito en esa clase?
    boolean existsByUsuarioUsuarioIdAndClaseClaseIdAndEstado(
            Integer usuarioId, Integer claseId, EstadoEnum estado);

    // Inscripciones de una clase según su estado (para tomar asistencia)
    List<Inscripcion> findByClaseClaseIdAndEstado(Integer claseId, EstadoEnum estado);

        // Verificar Inscripción Activa: clase + usuario + estado
    Optional<Inscripcion> findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(
            Integer claseId, Integer usuarioId, EstadoEnum estado);
}