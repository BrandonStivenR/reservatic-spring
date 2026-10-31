# ReservaTIC - Módulo de Reservas (Spring Boot)

Módulo web de reservas de salas y laboratorios de informática del proyecto ReservaTIC.
Evidencia GA7-220501096-AA3-EV01. Codificación de módulos del software stand-alone, web y móvil.

## Funcionalidades (CRUD)

- Listar reservas
- Crear reserva (formulario de solicitud)
- Editar reserva
- Eliminar reserva
- Comprobante de reserva
- Validación de cruce de horarios (evita reservar la misma sala en horas solapadas)

## Tecnologías

- Java 21
- Spring Boot 4.0.9 (Spring MVC, Spring Data JPA)
- Thymeleaf
- MySQL 8
- Maven

## Estructura

- `model`: entidades Usuario, Sala y Reserva
- `repository`: repositorios Spring Data JPA
- `controller`: ReservaController (rutas y lógica)
- `resources/templates`: vistas Thymeleaf
- `resources/static/css`: estilos

## Cómo ejecutarlo

1. Tener MySQL con la base `reservatic_db` (tablas `usuarios`, `salas` y `reservas`) y al menos una sala y un usuario registrados.
2. En `src/main/resources/application.properties`, escribir la contraseña de MySQL en `spring.datasource.password`.
3. Abrir el proyecto en VS Code y ejecutar `ReservaticSpringApplication`.
4. Entrar a `http://localhost:8080/`.

## Autor

Brandon Steven Robles Lozada