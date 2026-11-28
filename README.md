# GymManager API

> API REST para la gestión y automatización de procesos operativos de un gimnasio. Proyecto académico para la asignatura de Programación Orientada a Objetos.

## Descripción

**GymManager** es un backend que resuelve los problemas operativos comunes de un gimnasio: administra clases, entrenadores, miembros, horarios, inscripciones y control de asistencia, con operaciones CRUD completas (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) protegidas por autenticación JWT y control de acceso por rol.

## Tabla de contenido

- [Roles y funcionalidades](#roles-y-funcionalidades)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Endpoints](#endpoints)
- [Ejecución local con Maven](#ejecución-local-con-maven)
- [Pruebas](#pruebas)
- [Despliegue en Kubernetes (minikube)](#despliegue-en-kubernetes-minikube)
- [Solución de problemas](#solución-de-problemas)

---

## Roles y funcionalidades

El sistema implementa tres roles:

| Rol | Qué puede hacer |
|---|---|
| **Administrador** | Gestión completa de usuarios y roles; creación y administración de clases; asignación de entrenadores a las clases; configuración de horarios validando que no se traslapen. |
| **Entrenador** | Consultar los miembros inscritos en sus clases; registrar la asistencia; la inasistencia dispara una notificación automática por correo. |
| **Miembro** | Iniciar sesión; inscribirse a clases (con validación de cupos); cancelar inscripciones conservando el historial; consultar su historial de asistencia. |

Al arrancar por primera vez, la aplicación crea el rol y el usuario **Administrador** a partir de las propiedades `app.admin.*`.

---

## Tecnologías

- **Lenguaje:** Java 25
- **Framework:** Spring Boot 4.1.1 (Maven)
- **Persistencia:** Spring Data JPA + PostgreSQL, con migraciones versionadas en Flyway (`ddl-auto=validate`)
- **Seguridad:** Spring Security + JWT (jjwt 0.13.0), sesiones stateless
- **Documentación de la API:** springdoc-openapi (Swagger UI)
- **Correo:** Spring Mail (SMTP) + plantillas Thymeleaf
- **Pruebas:** JUnit 5 / Spring Boot Test
- **Contenedores y CI/CD:** Docker, Kubernetes y GitHub Actions

El modelo relacional cubre usuarios, roles, clases, horarios, inscripciones y control de asistencia, con claves primarias y foráneas que garantizan la integridad referencial.

---

## Estructura del proyecto

```text
GymManager-API/
├── .github/workflows/ci-cd.yml      # CI: pruebas + build de la imagen Docker
├── docs/
│   ├── postman/                     # Colección y entorno de Postman
│   └── insomnia/                    # Exportaciones de Insomnia
├── k8s/
│   ├── deployment.yaml              # Deployment (monta el Secret en /app/config)
│   └── service.yaml                 # Service NodePort (puerto 8080)
├── src/
│   ├── main/
│   │   ├── java/com/api/gymmanager/
│   │   │   ├── GymmanagerApplication.java
│   │   │   ├── config/              # Seed del administrador, Thymeleaf
│   │   │   ├── controller/          # Auth, Usuario, Rol, Clase, HorarioClase, Inscripcion, ControlAsistencia
│   │   │   ├── dto/                 # Requests/responses agrupados por entidad
│   │   │   ├── event/               # Evento y listener de inasistencia
│   │   │   ├── exception/           # Excepciones de negocio y GlobalExceptionHandler
│   │   │   ├── model/               # Entidades JPA y enums
│   │   │   ├── repository/          # Repositorios Spring Data
│   │   │   ├── security/            # SecurityConfig, JWT, UserDetailsService
│   │   │   └── service/             # Lógica de negocio y EmailService
│   │   └── resources/
│   │       ├── application.properties         # Configuración base (versionada)
│   │       ├── application-local.properties   # Secretos locales (NO versionado)
│   │       ├── db/migration/                  # V1__inicial.sql, V2__clase_restricciones.sql
│   │       └── templates/                     # Plantillas de correo (Thymeleaf)
│   └── test/java/com/api/gymmanager/          # Pruebas de controladores y servicios
├── Dockerfile                       # Build multi-stage (Maven + JRE 25)
├── mvnw / mvnw.cmd                  # Maven Wrapper
└── pom.xml
```

---

## Endpoints

Todos los endpoints requieren el header `Authorization: Bearer <token>`, salvo `/auth/**` y la documentación Swagger. El token se obtiene en `POST /auth/login`.

| Recurso | Método y ruta | Acceso |
|---|---|---|
| Auth | `POST /auth/login` | Público |
| Auth | `POST /auth/register` (crea un usuario con rol *Miembro*) | Público |
| Usuarios | `POST`, `GET` `/api/usuarios` · `GET`, `PUT`, `DELETE` `/api/usuarios/{id}` · `PATCH /api/usuarios/{id}/rol` | Administrador |
| Roles | `POST`, `PUT`, `DELETE` `/api/roles[/{id}]` | Administrador |
| Roles | `GET /api/roles`, `GET /api/roles/{id}` | Administrador, Miembro |
| Clases | `POST /api/clases`, `GET /api/clases/entrenadores`, `PUT`/`DELETE /api/clases/{id}` | Administrador |
| Clases | `GET /api/clases`, `GET /api/clases/{id}` | Cualquier usuario autenticado |
| Horarios | `POST /api/horarios-clases`, `PUT`/`DELETE /api/horarios-clases/{id}` | Administrador |
| Horarios | `GET /api/horarios-clases`, `GET /api/horarios-clases/{id}` | Cualquier usuario autenticado |
| Inscripciones | `POST /api/inscripciones`, `PATCH /api/inscripciones/{id}/cancelar` | Miembro |
| Asistencias | `GET /api/asistencias/clases/{claseId}/inscritos`, `POST /api/asistencias` | Entrenador |
| Asistencias | `GET /api/asistencias/mi-historial` | Miembro |

La referencia interactiva está en Swagger UI: <http://localhost:8080/swagger-ui/index.html>.
En `docs/postman/` y `docs/insomnia/` hay colecciones listas para importar (base URL: `http://localhost:8080`).

---

### Primer uso

El administrador inicial solo recibe el rol `Administrador`. Antes de que alguien pueda usar `POST /auth/register`, el rol **`Miembro`** debe existir en la base de datos. Con el token del administrador:

1. Crea los roles `Miembro` y `Entrenador` con `POST /api/roles`.
2. Los usuarios que se registren por `/auth/register` quedan como `Miembro`.
3. Para convertir a alguien en entrenador, usa `PATCH /api/usuarios/{id}/rol`.

---

## Ejecución local con Maven

Requisitos: **JDK 25** y una base de datos PostgreSQL accesible (las migraciones de Flyway se aplican al iniciar).

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

---

## Pruebas

```bash
./mvnw test -Dtest='!GymmanagerApplicationTests'
```

`GymmanagerApplicationTests` se excluye porque necesita conexión real a la base de datos; es el mismo comando que ejecuta el CI en cada `push` y `pull_request`.

---

## Despliegue en Kubernetes (minikube)

La imagen se construye dentro de minikube (`imagePullPolicy: Never`) y la configuración se inyecta como un Secret montado en `/app/config`, donde Spring Boot lee `application-local.properties` automáticamente.

### 0. Verificar que Docker funcione sin `sudo`

```bash
docker ps
```

Si aparece `permission denied`:

```bash
sudo usermod -aG docker $USER && newgrp docker
```

### 1. Instalar minikube (incluye su propio kubectl)

```bash
curl -LO https://github.com/kubernetes/minikube/releases/latest/download/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
rm minikube-linux-amd64
alias kubectl="minikube kubectl --"
```

El `alias` solo dura en esa terminal; para dejarlo fijo, agrégalo a `~/.bashrc`.

### 2. Ir al proyecto

```bash
cd GymManager-API
```

Verifica que exista `src/main/resources/application-local.properties`

### 3. Levantar el clúster

```bash
minikube start --driver=docker
kubectl get nodes
```

El nodo debe aparecer en `Ready`.

### 4. Construir la imagen dentro de minikube

```bash
minikube image build -t gymmanager-api:latest .
minikube image ls | grep gymmanager
```

La primera vez tarda unos minutos porque descarga Maven y las dependencias.

### 5. Crear el Secret con la configuración

```bash
kubectl create secret generic gymmanager-config \
  --from-file=application-local.properties=src/main/resources/application-local.properties
```

### 6. Desplegar

```bash
kubectl apply -f k8s/
kubectl rollout status deployment/gymmanager-api --timeout=240s
kubectl get pods,svc
```

Espera a que el pod muestre `1/1 Running`; puede tardar un minuto por Flyway y la conexión a la base de datos.

### 7. Exponer la API y probarla

```bash
kubectl port-forward svc/gymmanager-api 8080:8080
```

Deja esa terminal abierta y, desde otra:

```bash
curl -i http://localhost:8080/swagger-ui/index.html
```

En Postman o Insomnia usa `http://localhost:8080` como base.

### Operaciones frecuentes

| Tarea | Comando |
|---|---|
| Ver logs | `kubectl logs -f deploy/gymmanager-api` |
| Aplicar cambios de código | `minikube image build -t gymmanager-api:latest .` y luego `kubectl rollout restart deployment/gymmanager-api` |
| Actualizar la configuración | `kubectl delete secret gymmanager-config`, repetir el paso 5 y `kubectl rollout restart deployment/gymmanager-api` |
| Apagar todo | `kubectl delete -f k8s/` y `minikube stop` |
| Volver a levantar | `minikube start` y `kubectl port-forward svc/gymmanager-api 8080:8080` |

Si el pod no vuelve solo después de reiniciar: `kubectl rollout restart deployment/gymmanager-api`.

---

## Solución de problemas

| Síntoma | Causa y solución |
|---|---|
| `ErrImageNeverPull` | La imagen no está dentro de minikube. Repite el paso 4. |
| `ContainerCreating` eterno | Falta el Secret. Repite el paso 5. |
| `CrashLoopBackOff` | Revisa `kubectl logs deploy/gymmanager-api`. Casi siempre es la conexión a la base de datos (necesita internet) o una credencial incorrecta. |
| `OOMKilled` | El límite de memoria es 1Gi. Súbelo en `k8s/deployment.yaml`. |
| `El rol 'Miembro' no existe...` al registrar | Crea el rol `Miembro` como administrador (ver [Primer uso](#primer-uso)). |

Para diagnosticar, reúne la salida de `kubectl get pods` y de `kubectl logs deploy/gymmanager-api`.