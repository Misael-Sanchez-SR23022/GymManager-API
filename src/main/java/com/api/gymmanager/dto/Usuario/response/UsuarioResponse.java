package com.api.gymmanager.dto.Usuario.response;

import com.api.gymmanager.model.EnumModel.GeneroEnum;

public record UsuarioResponse(
        Integer usuarioId,
        String primerNombre,
        String segundoNombre,
        String correoElectronico,
        String nombreUsuario,
        GeneroEnum genero,
        Integer rolId,
        String nombreRol) {
}