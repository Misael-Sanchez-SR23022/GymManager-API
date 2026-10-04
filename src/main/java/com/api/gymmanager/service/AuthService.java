package com.api.gymmanager.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.gymmanager.dto.Auth.request.LoginRequest;
import com.api.gymmanager.dto.Auth.request.RegisterRequest;
import com.api.gymmanager.dto.Auth.response.LoginResponse;
import com.api.gymmanager.dto.Usuario.response.UsuarioResponse;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Rol;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.repository.RolRepository;
import com.api.gymmanager.repository.UsuarioRepository;
import com.api.gymmanager.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest dto) {
        Usuario entity = usuarioRepository.findByNombreUsuario(dto.nombreUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario " + dto.nombreUsuario() + " no encontrado"));

        if (!passwordEncoder.matches(dto.contrasenia(), entity.getContrasenia())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtService.generateToken(entity);

        return new LoginResponse(token, mapToResponse(entity));
    }

    public LoginResponse register(RegisterRequest dto) {

        if (usuarioRepository.findByNombreUsuario(dto.nombreUsuario()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un usuario con el nombre: " + dto.nombreUsuario());
        } else if (usuarioRepository.findByCorreoElectronico(dto.correoElectronico()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un usuario con el correo: " + dto.correoElectronico());
        }

        Rol rolPorDefecto = rolRepository.findByNombreRol("Miembro")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El rol 'Miembro' no existe en la base de datos. Créalo primero."));

        Usuario entity = new Usuario();

        entity.setPrimerNombre(dto.primerNombre());
        entity.setSegundoNombre(dto.segundoNombre());
        entity.setNombreUsuario(dto.nombreUsuario());
        entity.setCorreoElectronico(dto.correoElectronico());
        entity.setContrasenia(passwordEncoder.encode(dto.contrasenia()));
        entity.setGenero(dto.genero());
        entity.setRol(rolPorDefecto);

        Usuario saved = usuarioRepository.save(entity);

        String token = jwtService.generateToken(saved);

        return new LoginResponse(token, mapToResponse(entity));

    }

    private UsuarioResponse mapToResponse(Usuario entity) {
        return new UsuarioResponse(
                entity.getUsuarioId(),
                entity.getPrimerNombre(),
                entity.getSegundoNombre(),
                entity.getCorreoElectronico(),
                entity.getNombreUsuario(),
                entity.getGenero(),
                entity.getRol().getRolId(),
                entity.getRol().getNombreRol());
    }
}