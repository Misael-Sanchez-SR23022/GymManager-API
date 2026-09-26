package com.api.gymmanager.service;

import org.springframework.stereotype.Service;

import com.api.gymmanager.dto.Rol.request.CreateRolRequest;
import com.api.gymmanager.dto.Rol.request.SearchRolRequest;
import com.api.gymmanager.dto.Rol.response.CreateRolResponse;
import com.api.gymmanager.dto.Rol.response.SearchRolResponse;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Rol;
import com.api.gymmanager.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public CreateRolResponse createRol(CreateRolRequest dto) {
        if (rolRepository.findByNombreRol(dto.nombreRol()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un rol con el nombre: " + dto.nombreRol());
        }

        Rol entity = new Rol();
        entity.setNombreRol(dto.nombreRol());
        Rol saved = rolRepository.save(entity);
        return new CreateRolResponse(saved.getRolId(), saved.getNombreRol());
    }

    public SearchRolResponse searchRol(SearchRolRequest dto) {
        Rol entity = rolRepository.findByRolId(dto.rolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol " + dto.rolId() + " no encontrado"));

        return new SearchRolResponse(entity.getRolId(), entity.getNombreRol());
    }
    
}
