package com.api.gymmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gymmanager.dto.ControlAsistencia.response.ControlAsistenciaResponse;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Clase;
import com.api.gymmanager.model.ControlAsistencia;
import com.api.gymmanager.model.EnumModel.EstadoEnum;
import com.api.gymmanager.model.Inscripcion;
import com.api.gymmanager.repository.ClaseRepository;
import com.api.gymmanager.repository.ControlAsistenciaRepository;
import com.api.gymmanager.repository.InscripcionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ControlAsistenciaService {

    private final ControlAsistenciaRepository controlAsistenciaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ClaseRepository claseRepository;

    // Inscripciones activas de una clase (base de Registrar Asistencia, #35)
    @Transactional(readOnly = true)
    public List<Inscripcion> obtenerInscripcionesActivas(Integer claseId) {
        buscarClase(claseId); // valida que la clase exista
        return inscripcionRepository.findByClaseClaseIdAndEstado(claseId, EstadoEnum.Activa);
    }

    // Registros de asistencia de una inscripción
    @Transactional(readOnly = true)
    public List<ControlAsistenciaResponse> getAsistenciasByInscripcion(Integer inscripcionId) {
        buscarInscripcion(inscripcionId); // valida que la inscripción exista
        return controlAsistenciaRepository.findByInscripcionInscripcionId(inscripcionId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Validaciones de existencia (reutilizables por los siguientes issues)
    private Clase buscarClase(Integer id) {
        return claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase " + id + " no encontrada"));
    }

    private Inscripcion buscarInscripcion(Integer id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción " + id + " no encontrada"));
    }

    private ControlAsistenciaResponse mapToResponse(ControlAsistencia a) {
        return new ControlAsistenciaResponse(
                a.getAsistenciaId(),
                a.getInscripcion().getInscripcionId(),
                a.getInscripcion().getClase().getClaseId(),
                a.getInscripcion().getClase().getNombreClase(),
                a.getInscripcion().getUsuario().getUsuarioId(),
                a.getAsistencia(),
                a.getFechaAsistencia());
    }
}