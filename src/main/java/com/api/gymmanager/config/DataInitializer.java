package com.api.gymmanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.api.gymmanager.model.Rol;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.model.EnumModel.GeneroEnum;
import com.api.gymmanager.repository.RolRepository;
import com.api.gymmanager.service.UsuarioService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final DataSeeding dataSeeding;

    @Override
    public void run(String... args) throws Exception {

        Rol rolAdmin = rolRepository.findByNombreRol(dataSeeding.getRol().getNombreRol())
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombreRol(dataSeeding.getRol().getNombreRol());
                    return rolRepository.save(nuevoRol);
                });

        if (usuarioService.count() == 0) {
            Usuario administrador = new Usuario();

            administrador.setCorreoElectronico(dataSeeding.getCorreoElectronico());
            administrador.setContrasenia(passwordEncoder.encode(dataSeeding.getContrasenia()));
            administrador.setPrimerNombre(dataSeeding.getPrimerNombre());
            administrador.setSegundoNombre(dataSeeding.getSegundoNombre());
            administrador.setNombreUsuario(dataSeeding.getNombreUsuario());
            administrador.setGenero(GeneroEnum.valueOf(dataSeeding.getGenero()));
            administrador.setRol(rolAdmin);

            usuarioService.save(administrador);
        }
    }
}