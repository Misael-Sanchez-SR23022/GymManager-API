package com.api.gymmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.gymmanager.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario>findByUsuaioId(Integer usuarioId);
    Optional<Usuario>findByNombreUsuario(String nombreUsuario);
}
