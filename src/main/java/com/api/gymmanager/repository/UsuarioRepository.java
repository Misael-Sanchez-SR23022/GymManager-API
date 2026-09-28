package com.api.gymmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.gymmanager.model.Usuario;

@Repository 
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario> findByUsuarioId(Integer usuarioId);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
}
