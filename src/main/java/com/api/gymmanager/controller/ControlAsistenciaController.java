package com.api.gymmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.ControlAsistencia.request.CreateControlAsistenciaRequest;
import com.api.gymmanager.dto.ControlAsistencia.response.ControlAsistenciaResponse;
import com.api.gymmanager.dto.ControlAsistencia.response.InscritoResponse;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.service.ControlAsistenciaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class ControlAsistenciaController {

    private final ControlAsistenciaService controlAsistenciaService;

    @PreAuthorize("hasRole('Entrenador')")
    @GetMapping("/clases/{claseId}/inscritos")
    public ResponseEntity<List<InscritoResponse>> listarInscritos(
            @AuthenticationPrincipal Usuario entrenador,
            @PathVariable Integer claseId) {
        return ResponseEntity.ok(
                controlAsistenciaService.listarInscritos(claseId, entrenador.getUsuarioId()));
    }

    @PreAuthorize("hasRole('Entrenador')")
    @PostMapping
    public ResponseEntity<List<ControlAsistenciaResponse>> registrarAsistencia(
            @AuthenticationPrincipal Usuario entrenador,
            @Valid @RequestBody CreateControlAsistenciaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(controlAsistenciaService.registrarAsistencia(entrenador.getUsuarioId(), dto));
    }
}