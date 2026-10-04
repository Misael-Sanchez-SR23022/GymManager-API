package com.api.gymmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.Rol.request.CreateRolRequest;
import com.api.gymmanager.dto.Rol.request.SearchRolRequest;
import com.api.gymmanager.dto.Rol.request.UpdateRolRequest;
import com.api.gymmanager.dto.Rol.response.CreateRolResponse;
import com.api.gymmanager.dto.Rol.response.SearchRolResponse;
import com.api.gymmanager.dto.Rol.response.UpdateRolResponse;
import com.api.gymmanager.service.RolService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @PreAuthorize ("hasRole('Administrador')")
    @PostMapping
    public ResponseEntity<CreateRolResponse> createRol(@Valid @RequestBody CreateRolRequest dto) {
        CreateRolResponse response = rolService.createRol(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize ("hasAnyRole('Miembro', 'Administrador')")
    @GetMapping
    public ResponseEntity<List<CreateRolResponse>> getAllRoles() {
        List<CreateRolResponse> response = rolService.getAllRoles();
        return ResponseEntity.ok(response);
    }

     @PreAuthorize ("hasAnyRole('Miembro', 'Administrador')")
    @GetMapping("/{id}")
    public ResponseEntity<SearchRolResponse> searchRol(@PathVariable Integer id) {
        SearchRolRequest dto = new SearchRolRequest(id);
        SearchRolResponse response = rolService.searchRol(dto);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize ("hasRole('Administrador')")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateRolResponse> updateRol(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRolRequest dto) {
        UpdateRolResponse response = rolService.updateRol(id, dto);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize ("hasRole('Administrador')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Integer id) {
        rolService.deleteRol(id);
        return ResponseEntity.noContent().build();
    }
}