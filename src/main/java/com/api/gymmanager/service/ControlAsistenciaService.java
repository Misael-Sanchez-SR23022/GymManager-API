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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;

import com.api.gymmanager.dto.ControlAsistencia.request.AsistenciaItemRequest;
import com.api.gymmanager.dto.ControlAsistencia.request.CreateControlAsistenciaRequest;
import com.api.gymmanager.dto.ControlAsistencia.response.InscritoResponse;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.model.Usuario;

import org.springframework.context.ApplicationEventPublisher;

import com.api.gymmanager.event.InasistenciaRegistradaEvent;
import com.api.gymmanager.model.EnumModel.AsistenciaEnum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.api.gymmanager.dto.ControlAsistencia.response.HistorialClaseResponse;
import com.api.gymmanager.dto.ControlAsistencia.response.RegistroAsistenciaResponse;

@Service
@RequiredArgsConstructor
public class ControlAsistenciaService {

    private final ControlAsistenciaRepository controlAsistenciaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ClaseRepository claseRepository;
        private final ApplicationEventPublisher eventPublisher;

    // Inscripciones activas de una clase (base de Registrar Asistencia)
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

    // Paso 1 del caso de uso: mostrar los usuarios con inscripción activa
    @Transactional(readOnly = true)
    public List<InscritoResponse> listarInscritos(Integer claseId, Integer entrenadorId) {
        Clase clase = buscarClase(claseId);
        validarEntrenadorDeClase(clase, entrenadorId);
        return obtenerInscripcionesActivas(claseId).stream()
                .map(this::mapToInscrito)
                .toList();
    }

    // Caso de uso: Registrar Asistencia
    @Transactional
    public List<ControlAsistenciaResponse> registrarAsistencia(
            Integer entrenadorId, CreateControlAsistenciaRequest dto) {

        Clase clase = buscarClase(dto.claseId());
        validarEntrenadorDeClase(clase, entrenadorId);

        Set<Integer> procesados = new HashSet<>();
        List<ControlAsistencia> registros = new ArrayList<>();

        for (AsistenciaItemRequest item : dto.asistencias()) {
            if (!procesados.add(item.usuarioId())) {
                throw new BusinessRuleException(
                        "El usuario " + item.usuarioId() + " aparece más de una vez en la lista");
            }

            // <<include>> Verificar Inscripción Activa
            Inscripcion inscripcion = verificarInscripcionActiva(clase.getClaseId(), item.usuarioId());

            if (controlAsistenciaRepository.existsByInscripcionInscripcionIdAndFechaAsistencia(
                    inscripcion.getInscripcionId(), dto.fechaAsistencia())) {
                throw new ResourceAlreadyExistsException(
                        "El usuario " + item.usuarioId() + " ya tiene asistencia registrada el "
                                + dto.fechaAsistencia());
            }

            ControlAsistencia registro = new ControlAsistencia();
            registro.setInscripcion(inscripcion);
            registro.setAsistencia(item.asistencia());
            registro.setFechaAsistencia(dto.fechaAsistencia());
            registros.add(registro);
        }


               List<ControlAsistencia> guardados;
        try {
            guardados = controlAsistenciaRepository.saveAll(registros);
        } catch (DataAccessException e) {
            throw new IllegalStateException("No se pudo guardar la asistencia", e);
        }

        // <<extend>> Notificar Inasistencia: un evento por cada usuario ausente
        guardados.stream()
                .filter(r -> r.getAsistencia() == AsistenciaEnum.Falto)
                .forEach(this::publicarInasistencia);

        return guardados.stream().map(this::mapToResponse).toList();
    }

    // Verificar Inscripción Activa: clase_id + usuario_id -> inscripcion
    private Inscripcion verificarInscripcionActiva(Integer claseId, Integer usuarioId) {
        return inscripcionRepository
                .findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(claseId, usuarioId, EstadoEnum.Activa)
                .orElseThrow(() -> new BusinessRuleException(
                        "El usuario " + usuarioId + " no tiene inscripción activa en esta clase"));
    }

    // Solo el entrenador asignado a la clase puede gestionar su asistencia
    private void validarEntrenadorDeClase(Clase clase, Integer entrenadorId) {
        if (!clase.getEntrenador().getUsuarioId().equals(entrenadorId)) {
            throw new AccessDeniedException(
                    "Solo el entrenador asignado a la clase puede gestionar su asistencia");
        }
    }

    private InscritoResponse mapToInscrito(Inscripcion i) {
        Usuario u = i.getUsuario();
        String nombre = u.getPrimerNombre()
                + (u.getSegundoNombre() != null ? " " + u.getSegundoNombre() : "");
        return new InscritoResponse(i.getInscripcionId(), u.getUsuarioId(), nombre);
    }
        private void publicarInasistencia(ControlAsistencia registro) {
        Usuario u = registro.getInscripcion().getUsuario();
        eventPublisher.publishEvent(new InasistenciaRegistradaEvent(
                u.getUsuarioId(),
                nombreCompleto(u),
                u.getCorreoElectronico(),
                registro.getInscripcion().getClase().getNombreClase(),
                registro.getFechaAsistencia()));
    }

    private String nombreCompleto(Usuario u) {
        return u.getPrimerNombre()
                + (u.getSegundoNombre() != null ? " " + u.getSegundoNombre() : "");
    }
        // Caso de uso: Consultar Asistencia (solo lectura), agrupada por clase
    @Transactional(readOnly = true)
    public List<HistorialClaseResponse> consultarHistorial(Integer usuarioId) {
        List<ControlAsistencia> registros = controlAsistenciaRepository
                .findByInscripcionUsuarioUsuarioIdOrderByFechaAsistenciaDesc(usuarioId);

        // Agrupa por clase conservando el orden (lo más reciente primero)
        Map<Integer, List<ControlAsistencia>> porClase = registros.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getInscripcion().getClase().getClaseId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        // Sin registros, devuelve una lista vacía (historial vacío)
        return porClase.values().stream()
                .map(this::mapToHistorialClase)
                .toList();
    }

    private HistorialClaseResponse mapToHistorialClase(List<ControlAsistencia> registros) {
        Clase clase = registros.get(0).getInscripcion().getClase();

        List<RegistroAsistenciaResponse> items = registros.stream()
                .map(r -> new RegistroAsistenciaResponse(
                        r.getAsistenciaId(), r.getFechaAsistencia(), r.getAsistencia()))
                .toList();

        long asistio = registros.stream()
                .filter(r -> r.getAsistencia() == AsistenciaEnum.Asistio)
                .count();

        return new HistorialClaseResponse(
                clase.getClaseId(), clase.getNombreClase(),
                asistio, registros.size() - asistio, items);
    }
}
