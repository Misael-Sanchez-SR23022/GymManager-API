package com.api.gymmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gymmanager.dto.Inscripcion.response.CreateInscripcionResponse;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Clase;
import com.api.gymmanager.model.EnumModel.EstadoEnum;
import com.api.gymmanager.model.Inscripcion;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.repository.ClaseRepository;
import com.api.gymmanager.repository.InscripcionRepository;
import com.api.gymmanager.repository.UsuarioRepository;

import java.time.LocalDateTime;

import com.api.gymmanager.dto.Inscripcion.request.CreateInscripcionRequest;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final ClaseRepository claseRepository;
    private final UsuarioRepository usuarioRepository;

    // Cuenta las inscripciones activas de una clase (base de Validar Cupo)
    @Transactional(readOnly = true)
    public long contarInscripcionesActivas(Integer claseId) {
        buscarClase(claseId);
        return inscripcionRepository.countByClaseClaseIdAndEstado(claseId, EstadoEnum.Activa);
    }

    // ¿El usuario ya tiene una inscripción activa en esa clase?
    @Transactional(readOnly = true)
    public boolean existeInscripcionActiva(Integer usuarioId, Integer claseId) {
        buscarUsuario(usuarioId);
        buscarClase(claseId);
        return inscripcionRepository.existsByUsuarioUsuarioIdAndClaseClaseIdAndEstado(
                usuarioId, claseId, EstadoEnum.Activa);
    }

    @Transactional(readOnly = true)
    public CreateInscripcionResponse getInscripcionById(Integer id) {
        return mapToResponse(buscarInscripcion(id));
    }

    // Validaciones de existencia (reutilizables por los siguientes issues)
    private Clase buscarClase(Integer id) {
        return claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase " + id + " no encontrada"));
    }

    private Usuario buscarUsuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario " + id + " no encontrado"));
    }

    private Inscripcion buscarInscripcion(Integer id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción " + id + " no encontrada"));
    }

    private CreateInscripcionResponse mapToResponse(Inscripcion i) {
        return new CreateInscripcionResponse(
                i.getInscripcionId(),
                i.getClase().getClaseId(),
                i.getClase().getNombreClase(),
                i.getUsuario().getUsuarioId(),
                i.getFechaRegistro(),
                i.getEstado());
    }

    // Caso de uso: Inscribirse en Clases
    @Transactional
    public CreateInscripcionResponse inscribirse(Integer usuarioId, CreateInscripcionRequest dto) {
        Usuario usuario = buscarUsuario(usuarioId);
        Clase clase = buscarClase(dto.claseId());

        // Flujo alterno: ya está inscrito, no se duplica
        if (inscripcionRepository.existsByUsuarioUsuarioIdAndClaseClaseIdAndEstado(
                usuarioId, clase.getClaseId(), EstadoEnum.Activa)) {
            throw new ResourceAlreadyExistsException(
                    "Ya estás inscrito en la clase '" + clase.getNombreClase() + "'");
        }

        // <<include>> Validar Cupo Disponible
        validarCupo(clase);

        Inscripcion entity = new Inscripcion();
        entity.setUsuario(usuario);
        entity.setClase(clase);
        entity.setFechaRegistro(LocalDateTime.now());
        entity.setEstado(EstadoEnum.Activa);

        return mapToResponse(inscripcionRepository.save(entity));
    }

    // Validar Cupo Disponible
    private void validarCupo(Clase clase) {
        long activas = inscripcionRepository
                .countByClaseClaseIdAndEstado(clase.getClaseId(), EstadoEnum.Activa);

        if (activas >= clase.getCapacidadClase()) {
            throw new BusinessRuleException(
                    "La clase '" + clase.getNombreClase() + "' está llena (capacidad: "
                            + clase.getCapacidadClase() + ")");
        }
    }
        // Caso de uso: Cancelar Inscripción (conserva el historial)
    @Transactional
    public CreateInscripcionResponse cancelarInscripcion(Integer inscripcionId, Integer usuarioId) {
        Inscripcion inscripcion = buscarInscripcion(inscripcionId);

        // Un miembro solo puede cancelar sus propias inscripciones
        if (!inscripcion.getUsuario().getUsuarioId().equals(usuarioId)) {
            throw new AccessDeniedException("No puedes cancelar la inscripción de otro miembro");
        }

        if (inscripcion.getEstado() == EstadoEnum.Cancelada) {
            throw new BusinessRuleException("La inscripción " + inscripcionId + " ya está cancelada");
        }

        // Solo cambia el estado: no se elimina el registro ni sus control_asistencia
        inscripcion.setEstado(EstadoEnum.Cancelada);
        return mapToResponse(inscripcionRepository.save(inscripcion));
    }
}