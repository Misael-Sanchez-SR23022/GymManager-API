package com.api.gymmanager.dto.Usuario.request;

import com.api.gymmanager.model.EnumModel.GeneroEnum;

import jakarta.validation.constraints.Email;

public record UpdateUsuarioRequest(
    String primerNombre,
    String segundoNombre,
    @Email String correoElectronico,
    String nombreUsuario,
    GeneroEnum genero,
    Integer rolId
) {}

