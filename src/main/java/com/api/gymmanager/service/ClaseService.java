package com.api.gymmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gymmanager.dto.Clase.request.CreateClaseRequest;
import com.api.gymmanager.dto.Clase.request.UpdateClaseRequest;
import com.api.gymmanager.dto.Clase.response.ClaseResponse;
import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Clase;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.repository.ClaseRepository;
import com.api.gymmanager.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaseService {

    private static final String ROL_ENTRENADOR = "Entrenador";
    private static final String MSG_SIN_ENTRENADORES =
            "No hay entrenadores registrados. Registre uno antes de gestionar clases.";

    private final ClaseRepository claseRepository;
    private final UsuarioRepository usuarioRepository;

    // Asignar Entrenador, paso 1: filtrar usuarios con rol entrenador
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getEntrenadores() {
        List<Usuario> entrenadores = usuarioRepository.findByRolNombreRol(ROL_ENTRENADOR);

        if (entrenadores.isEmpty()) {
            throw new BusinessRuleException(MSG_SIN_ENTRENADORES);
        }

        return entrenadores.stream()
                .map(u -> new UsuarioResponse(
                        u.getUsuarioId(),
                        u.getPrimerNombre(),
                        u.getSegundoNombre(),
                        u.getCorreoElectronico(),
                        u.getNombreUsuario(),
                        u.getGenero(),
                        u.getRol().getRolId(),
                        u.getRol().getNombreRol()))
                .toList();
    }

    @Transactional
    public ClaseResponse createClase(CreateClaseRequest dto) {
        Usuario entrenador = validarEntrenador(dto.entrenadorId());

        Clase entity = new Clase();
        entity.setNombreClase(dto.nombreClase());
        entity.setCapacidadClase(dto.capacidadClase());
        entity.setEntrenador(entrenador);

        return mapToResponse(claseRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ClaseResponse> getAllClases() {
        return claseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaseResponse getClaseById(Integer id) {
        return mapToResponse(buscarClase(id));
    }

    @Transactional
    public ClaseResponse updateClase(Integer id, UpdateClaseRequest dto) {
        Clase entity = buscarClase(id);
        Usuario entrenador = validarEntrenador(dto.entrenadorId());

        entity.setNombreClase(dto.nombreClase());
        entity.setCapacidadClase(dto.capacidadClase());
        entity.setEntrenador(entrenador);

        return mapToResponse(claseRepository.save(entity));
    }

    @Transactional
    public void deleteClase(Integer id) {
        claseRepository.delete(buscarClase(id));
    }

    private Clase buscarClase(Integer id) {
        return claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase " + id + " no encontrada"));
    }

    // Asignar Entrenador, pasos 2 y 3: validar y asignar (obligatorio al guardar)
    private Usuario validarEntrenador(Integer entrenadorId) {
        if (!usuarioRepository.existsByRolNombreRol(ROL_ENTRENADOR)) {
            throw new BusinessRuleException(MSG_SIN_ENTRENADORES);
        }

        Usuario usuario = usuarioRepository.findById(entrenadorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entrenador " + entrenadorId + " no encontrado"));

        if (!ROL_ENTRENADOR.equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            throw new BusinessRuleException(
                    "El usuario " + entrenadorId + " no tiene el rol de entrenador");
        }
        return usuario;
    }

    private ClaseResponse mapToResponse(Clase entity) {
        Usuario e = entity.getEntrenador();
        String nombre = e.getPrimerNombre()
                + (e.getSegundoNombre() != null ? " " + e.getSegundoNombre() : "");
        return new ClaseResponse(
                entity.getClaseId(),
                entity.getNombreClase(),
                entity.getCapacidadClase(),
                e.getUsuarioId(),
                nombre);
    }
}