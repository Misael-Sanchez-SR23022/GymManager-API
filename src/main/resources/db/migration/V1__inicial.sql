CREATE TYPE genero_usuario AS ENUM ('Femenino', 'Masculino');
CREATE TYPE semana AS ENUM ('Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo');
CREATE TYPE asistencia_clase AS ENUM ('Asistio', 'Falto');
CREATE TYPE estado_inscripcion AS ENUM ('Activa', 'Cancelada');

CREATE TABLE rol (
    rol_id SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(30) NOT NULL
);

CREATE TABLE usuario (
    usuario_id SERIAL PRIMARY KEY,
    rol_id INT REFERENCES rol,
    primer_nombre VARCHAR(30) NOT NULL,
    segundo_nombre VARCHAR(30) NOT NULL,
    nombre_usuario VARCHAR(30) UNIQUE NOT NULL,
    contrasenia VARCHAR(255) NOT NULL,
    correo_electronico VARCHAR(100) UNIQUE NOT NULL,
    genero genero_usuario NOT NULL
);

CREATE TABLE clase (
    clase_id SERIAL PRIMARY KEY,
    entrenador_id INT REFERENCES usuario,
    nombre_clase VARCHAR(50) NOT NULL,
    capacidad_clase INT
);

CREATE TABLE horario_clase (
    horario_id SERIAL PRIMARY KEY,
    clase_id INT REFERENCES clase,
    dias_clase semana NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL
);

CREATE TABLE inscripcion (
    inscripcion_id SERIAL PRIMARY KEY,
    clase_id INT REFERENCES clase,
    usuario_id INT REFERENCES usuario,
    fecha_registro TIMESTAMP NOT NULL,
    estado estado_inscripcion NOT NULL DEFAULT 'Activa'
);

CREATE TABLE control_asistencia (
    asistencia_id SERIAL PRIMARY KEY,
    inscripcion_id INT REFERENCES inscripcion,
    asistencia asistencia_clase NOT NULL,
    fecha_asistencia DATE NOT NULL
);