# GestionTurnos

Sistema de gestión de turnos para consultorios (pacientes, profesionales,
especialidades, obras sociales, horarios y reportes en PDF con JasperReports).

## Stack

- Java 17 · Spring Boot 4.1 (Web MVC, Data JPA, Security, Thymeleaf)
- MySQL 8
- JasperReports (informe de turnos en PDF)
- Docker + Fly.io

## Ejecutar en local

1. Crear la base `test9` en un MySQL local (o ajustar `src/main/resources/application.properties`).
2. Ejecutar:

```bash
./mvnw spring-boot:run
```

3. Abrir <http://localhost:8091/gestionturnos>.

Si la base está vacía, al arrancar se crean las tablas, los roles y el usuario
administrador. La contraseña inicial se toma de `ADMIN_PASSWORD`; si no está
definida, se genera una aleatoria y se muestra en el log.

## Ejecutar con Docker

```bash
docker compose up --build
```

Abrir <http://localhost:8091/gestionturnos> (`admin` / `admin1234`).

## Desplegar en Fly.io

Ver **[DEPLOY.md](DEPLOY.md)**. Incluye la creación de la base MySQL en Fly,
los secretos, el despliegue y el deploy automático desde GitHub.
