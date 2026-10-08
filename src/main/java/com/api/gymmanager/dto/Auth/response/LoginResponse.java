package com.api.gymmanager.dto.Auth.response;

import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;

public record LoginResponse(
    String token,    
    UsuarioResponse usuario
) {}