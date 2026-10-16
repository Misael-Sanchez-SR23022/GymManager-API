package com.api.gymmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.gymmanager.model.Clase;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Integer> {
}