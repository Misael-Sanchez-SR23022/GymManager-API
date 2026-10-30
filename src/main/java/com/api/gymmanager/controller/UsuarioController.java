package com.api.gymmanager.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.Usuario.request.CreateUsuarioRequest;
import com.api.gymmanager.dto.Usuario.request.SearchUsuarioRequest;
import com.api.gymmanager.dto.Usuario.request.UpdateRolUsuarioRequest;
import com.api.gymmanager.dto.Usuario.request.UpdateUsuarioRequest;
import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.service.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('Administrador')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody CreateUsuarioRequest dto) {
        UsuarioResponse response = usuarioService.createUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> getAllUsuarios(
            @RequestParam(required = false) String rol) {
        return ResponseEntity.ok(usuarioService.getAllUsuarios(rol));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> searchUsuario(@PathVariable Integer id) {
        SearchUsuarioRequest dto = new SearchUsuarioRequest(id, null);
        UsuarioResponse response = usuarioService.searchUsuario(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUsuarioRequest dto) {
        UsuarioResponse response = usuarioService.updateUsuario(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRolUsuarioRequest dto,
            @AuthenticationPrincipal Usuario actual) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, dto, actual.getUsuarioId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
