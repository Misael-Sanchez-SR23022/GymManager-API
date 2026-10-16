package com.api.gymmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.Clase.request.CreateClaseRequest;
import com.api.gymmanager.dto.Clase.request.UpdateClaseRequest;
import com.api.gymmanager.dto.Clase.response.ClaseResponse;
import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;
import com.api.gymmanager.service.ClaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService claseService;

    @PostMapping
    public ResponseEntity<ClaseResponse> createClase(@Valid @RequestBody CreateClaseRequest dto) {
        ClaseResponse response = claseService.createClase(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ClaseResponse>> getAllClases() {
        return ResponseEntity.ok(claseService.getAllClases());
    }

    // Asignar Entrenador: lista de usuarios con rol Entrenador
    @GetMapping("/entrenadores")
    public ResponseEntity<List<UsuarioResponse>> getEntrenadores() {
        return ResponseEntity.ok(claseService.getEntrenadores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaseResponse> getClase(@PathVariable Integer id) {
        return ResponseEntity.ok(claseService.getClaseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponse> updateClase(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateClaseRequest dto) {
        return ResponseEntity.ok(claseService.updateClase(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClase(@PathVariable Integer id) {
        claseService.deleteClase(id);
        return ResponseEntity.noContent().build();
    }
}