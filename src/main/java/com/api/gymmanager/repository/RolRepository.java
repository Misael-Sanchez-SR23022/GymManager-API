package com.api.gymmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.gymmanager.model.Rol;

@Repository 
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByRolId(Integer rolId);
    Optional<Rol> findByNombreRol(String nombreRol);
}