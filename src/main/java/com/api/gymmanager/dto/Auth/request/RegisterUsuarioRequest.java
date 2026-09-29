package com.api.gymmanager.dto.Auth.request;

import com.api.gymmanager.model.EnumModel.GeneroEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterUsuarioRequest(
    @NotBlank String primerNombre,
    String segundoNombre,
    @NotBlank @Email String correoElectronico,
    @NotBlank String nombreUsuario,
    @NotBlank @Size(min = 8) String contrasenia,
    @NotNull GeneroEnum genero
) {}
