package com.api.gymmanager.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.api.gymmanager.model.EnumModel.GeneroEnum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity 
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "primer_nombre",nullable = false, length = 50)
    private String primerNombre;

    @Column(name = "segundo_nombre",nullable = false, length = 50)
    private String segundoNombre;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "contrasenia",nullable = false, length = 255)
    private String contrasenia;

    @Column(name = "correo_electronico", nullable = false, unique = true)
    private String correoElectronico;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "genero",nullable = false)
    private GeneroEnum genero;
}
