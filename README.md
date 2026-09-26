# GymManager API

> API orientada a la gestión y automatización de procesos operativos para gimnasios, desarrollada como proyecto académico para la asignatura de Programación Orientada a Objetos.

## Descripción del Proyecto

**GymManager** es una solución backend diseñada para resolver los problemas operativos comunes en un gimnasio. Permite administrar de forma eficiente las clases, los entrenadores, los miembros y el control de asistencia mediante operaciones `CRUD` completas (`GET`, `POST`, `PUT`, `DELETE`).

---

## Roles y Funcionalidades del Sistema

El sistema implementa tres roles de usuario principales:

1. **Administrador:**
   - Gestión completa de usuarios y roles.
   - Creación y administración de clases.
   - Asignación de entrenadores a las clases.
   - Configuración y validación de horarios (evitando traslapes).

2. **Entrenador:**
   - Consulta de clases asignadas y miembros inscritos.
   - Registro de asistencia de los miembros.
   - Notificación automática en caso de inasistencia.

3. **Miembro:**
   - Autenticación e inicio de sesión.
   - Inscripción a clases (con validación automática de cupos disponibles).
   - Cancelación de inscripciones manteniendo el historial.
   - Consulta personal del historial de asistencia.

---

## Tecnologías y Diseño
- **Arquitectura:** API RESTful orientada a objetos.
- **Lenguaje:** Java 25
- **Framework:** Spring Boot (con Maven)
- **Base de Datos:** Modelo relacional normalizado con control de integridad referencial (claves primarias y foráneas para usuarios, roles, clases, horarios, inscripciones y control de asistencia haciendo uso de PostgreSQL