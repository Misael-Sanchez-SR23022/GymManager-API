package com.api.gymmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.admin")
@Getter
@Setter
public class DataSeeding {

    private String primerNombre;
    private String segundoNombre;
    private String nombreUsuario;
    private String contrasenia;

    @Email
    private String correoElectronico;

    private String genero;
    private RolProperties rol;

    @Getter
    @Setter
    public static class RolProperties {
        private String nombreRol;
    }
}