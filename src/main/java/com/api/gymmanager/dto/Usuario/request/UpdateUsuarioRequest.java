package com.api.gymmanager.dto.Usuario.request;

import com.api.gymmanager.model.EnumModel.GeneroEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUsuarioRequest(
    @NotBlank String primerNombre,
    String segundoNombre,
    @NotBlank @Email String correoElectronico,
    @NotBlank String nombreUsuario,
    @NotNull GeneroEnum genero
) {}

