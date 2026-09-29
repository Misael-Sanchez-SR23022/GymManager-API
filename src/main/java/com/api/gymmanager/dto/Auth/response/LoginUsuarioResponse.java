package com.api.gymmanager.dto.Auth.response;

import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;

public record LoginUsuarioResponse(
    String token,
    String tipoToken,      
    UsuarioResponse usuario
) {}