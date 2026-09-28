package com.api.gymmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gymmanager.dto.Usuario.request.CreateUsuarioRequest;
import com.api.gymmanager.dto.Usuario.request.SearchUsuarioRequest;
import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;
//import com.api.gymmanager.service.RolService;
import com.api.gymmanager.service.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    //private final RolService rolService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody CreateUsuarioRequest dto) {
        UsuarioResponse response = usuarioService.createUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> searchUsuario(@PathVariable Integer id) {
        SearchUsuarioRequest dto = new SearchUsuarioRequest(id, null);
        UsuarioResponse response = usuarioService.searchUsuario(dto);
        return ResponseEntity.ok(response);
    }
}
