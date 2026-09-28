package com.api.gymmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.gymmanager.dto.Usuario.request.CreateUsuarioRequest;
import com.api.gymmanager.dto.Usuario.request.SearchUsuarioRequest;
import com.api.gymmanager.dto.Usuario.request.UpdateUsuarioRequest;
import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Rol;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.repository.RolRepository;
import com.api.gymmanager.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioResponse createUsuario(CreateUsuarioRequest dto) {
        if (usuarioRepository.findByNombreUsuario(dto.nombreUsuario()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un usuario con el nombre: " + dto.nombreUsuario());
        } else if (usuarioRepository.findByCorreoElectronico(dto.correoElectronico()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un usuario con el correo: " + dto.correoElectronico());
        }

        Rol rolPorDefecto = rolRepository.findByNombreRol("Miembro")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El rol 'Miembro' no existe en la base de datos. Créalo primero."));

        Usuario entity = new Usuario();

        entity.setPrimerNombre(dto.primerNombre());
        entity.setSegundoNombre(dto.segundoNombre());
        entity.setNombreUsuario(dto.nombreUsuario());
        entity.setCorreoElectronico(dto.correoElectronico());
        entity.setContrasenia(dto.contrasenia());
        entity.setGenero(dto.genero());
        entity.setRol(rolPorDefecto);

        Usuario saved = usuarioRepository.save(entity);
        return mapToResponse(saved);
    }

    public UsuarioResponse searchUsuario(SearchUsuarioRequest dto) {
        Usuario entity = usuarioRepository.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario " + dto.usuarioId() + " no encontrado"));

        return mapToResponse(entity);
    }

    public UsuarioResponse searchUsuarioByName(SearchUsuarioRequest dto) {
        Usuario entity = usuarioRepository.findByNombreUsuario(dto.nombreUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario " + dto.nombreUsuario() + " no encontrado"));

        return mapToResponse(entity);
    }

    public List<UsuarioResponse> getAllUsuarios() {
    return usuarioRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
}
    public UsuarioResponse updateUsuario(Integer id, UpdateUsuarioRequest dto) {
        Usuario entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario" + id + "no encontrado"));

        usuarioRepository.findByNombreUsuario(dto.nombreUsuario()).ifPresent(existente -> {
            if (!existente.getUsuarioId().equals(id)) {
                throw new ResourceAlreadyExistsException("Ya existe un usaurio con el nombre: " + dto.nombreUsuario());
            }
        });

        entity.setPrimerNombre(dto.primerNombre());
        entity.setSegundoNombre(dto.segundoNombre());
        entity.setNombreUsuario(dto.nombreUsuario());
        entity.setCorreoElectronico(dto.correoElectronico());
        entity.setGenero(dto.genero());
        Usuario updated = usuarioRepository.save(entity);

        return mapToResponse(updated);
    }

    public void deleteUsuario(Integer id) {
        Usuario entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usaurio con ID" + id + "no encontrado"));

        usuarioRepository.delete(entity);
    }

    private UsuarioResponse mapToResponse(Usuario entity) {
        return new UsuarioResponse(
                entity.getUsuarioId(),
                entity.getPrimerNombre(),
                entity.getSegundoNombre(),
                entity.getCorreoElectronico(),
                entity.getNombreUsuario(),
                entity.getGenero(),
                entity.getRol().getRolId(),
                entity.getRol().getNombreRol());
    }

}