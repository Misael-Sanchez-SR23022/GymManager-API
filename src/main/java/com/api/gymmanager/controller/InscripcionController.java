package com.api.gymmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.Inscripcion.request.CreateInscripcionRequest;
import com.api.gymmanager.dto.Inscripcion.response.CreateInscripcionResponse;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.service.InscripcionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PreAuthorize("hasRole('Miembro')")
    @PostMapping
    public ResponseEntity<CreateInscripcionResponse> inscribirse(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CreateInscripcionRequest dto) {
        CreateInscripcionResponse response = inscripcionService.inscribirse(usuario.getUsuarioId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
        @PreAuthorize("hasRole('Miembro')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CreateInscripcionResponse> cancelarInscripcion(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                inscripcionService.cancelarInscripcion(id, usuario.getUsuarioId()));
    }
}