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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.HorarioClases.request.CreateHorarioClaseRequest;
import com.api.gymmanager.dto.HorarioClases.request.UpdateHorarioClaseRequest;
import com.api.gymmanager.dto.HorarioClases.response.HorarioClaseResponse;
import com.api.gymmanager.service.HorarioClaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/horarios-clases")
@RequiredArgsConstructor
public class HorarioClaseController {

    private final HorarioClaseService horarioClaseService;

    @PreAuthorize("hasRole('Administrador')")
    @PostMapping
    public ResponseEntity<HorarioClaseResponse> createHorarioClase(
            @Valid @RequestBody CreateHorarioClaseRequest dto,
            @RequestParam(defaultValue = "false") boolean confirmarTraslape) {

        HorarioClaseResponse response = horarioClaseService.createHorarioClase(dto, confirmarTraslape);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HorarioClaseResponse>> getAllHorarios() {
        return ResponseEntity.ok(horarioClaseService.getAllHorarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioClaseResponse> getHorarioById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(horarioClaseService.getHorarioById(id));
    }

    @PreAuthorize("hasRole('Administrador')")
    @PutMapping("/{id}")
    public ResponseEntity<HorarioClaseResponse> updateHorarioClase(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateHorarioClaseRequest dto) {

        return ResponseEntity.ok(
                horarioClaseService.updateHorarioClase(id, dto));
    }

    @PreAuthorize("hasRole('Administrador')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorarioClase(
            @PathVariable Integer id) {

        horarioClaseService.deleteHorarioClase(id);

        return ResponseEntity.noContent().build();
    }
}