# transparency-service

## 1. Descripción

El Transparency Service es un microservicio de solo lectura encargado de
exponer la trazabilidad y registros de transparencia de una elección
(actas, validaciones y eventos).

Forma parte del lado de consulta bajo el enfoque CQRS e implementa una
capa de cache con Redis, incluyendo mecanismos de resiliencia para
tolerar fallos del servicio de cache.

------------------------------------------------------------------------

## 2. Tecnologías

-   Java 21
-   Spring Boot 3.x
-   Spring Web
-   Spring Data JPA
-   PostgreSQL
-   Redis
-   Resilience4j (Circuit Breaker)
-   Flyway
-   Lombok
-   Springdoc OpenAPI (Swagger)
-   Maven

------------------------------------------------------------------------

## 3. Arquitectura

Arquitectura por capas:

-   Controller: Exposición de endpoints REST
-   Service: Lógica de negocio y orquestación
-   Repository: Acceso a datos
-   Cache Adapter: Integración con Redis
-   Circuit Breaker: Manejo de fallos en cache
-   Mapper: Transformación de entidades a DTOs

------------------------------------------------------------------------

## 4. Estrategia de Cache

Se implementa el patrón cache-aside con resiliencia:

1.  Se consulta Redis
2.  Si no existe o falla, se consulta la base de datos
3.  Se almacena el resultado en cache
4.  En caso de fallo de Redis, el sistema continúa operando con DB

------------------------------------------------------------------------

## 5. Resiliencia (Circuit Breaker)

Se implementa Circuit Breaker con Resilience4j:

-   Detecta fallos en Redis
-   Evita llamadas innecesarias a servicios caídos
-   Permite fallback automático hacia la base de datos
-   Mejora la latencia en escenarios de fallo

Estados:

-   CLOSED → operación normal
-   OPEN → Redis deshabilitado temporalmente
-   HALF-OPEN → prueba de recuperación

------------------------------------------------------------------------

## 6. Versionamiento API

/api/v1/\*

------------------------------------------------------------------------

## 7. Variables de entorno

DB_URL=jdbc:postgresql://localhost:5433/transparency_db\
DB_USER=transparency_user\
DB_PASSWORD=123456

REDIS_HOST=localhost\
REDIS_PORT=6380

PORT=8084

------------------------------------------------------------------------

## 8. Base de datos

CREATE DATABASE transparency_db;\
CREATE USER transparency_user WITH PASSWORD '123456';\
GRANT ALL PRIVILEGES ON DATABASE transparency_db TO transparency_user;

`\c t`{=tex}ransparency_db\
GRANT ALL ON SCHEMA public TO transparency_user;

------------------------------------------------------------------------

## 9. Migraciones (Flyway)

Ubicación:

src/main/resources/db/migration

### V1\_\_init.sql

CREATE TABLE transparency_record ( id SERIAL PRIMARY KEY, election_id
BIGINT, event_type VARCHAR(100), description TEXT, timestamp TIMESTAMP
);

### V2\_\_seed.sql

INSERT INTO transparency_record (election_id, event_type, description,
timestamp) VALUES (1, 'ACTA_REGISTRADA', 'Acta subida desde mesa 101',
NOW()), (1, 'RESULTADO_VALIDADO', 'Validación completada', NOW());

------------------------------------------------------------------------

## 10. Redis

sudo systemctl start redis-server\
redis-cli ping

Respuesta esperada: PONG

------------------------------------------------------------------------

## 11. Ejecución

export \$(grep -v '\^#' .env \| xargs)\
mvn spring-boot:run

------------------------------------------------------------------------

## 12. Swagger

http://localhost:8084/swagger-ui.html

------------------------------------------------------------------------

## 13. Endpoint

GET /api/v1/transparency?electionId=1

------------------------------------------------------------------------

## 14. Respuesta

{ "electionId": 1, "records": \[ { "eventType": "ACTA_REGISTRADA",
"description": "Acta subida desde mesa 101", "timestamp":
"2026-01-01T10:00:00" } \] }

------------------------------------------------------------------------

## 15. Observabilidad

Logging estructurado:

-   CACHE HIT
-   CACHE MISS
-   CACHE STORE
-   CACHE ERROR
-   CACHE FALLBACK
-   Circuit Breaker events (OPEN, CLOSED, HALF-OPEN)

------------------------------------------------------------------------

## 16. Estado

Microservicio funcional, resiliente y listo para integración:

-   API REST operativa
-   PostgreSQL integrado
-   Redis con tolerancia a fallos
-   Circuit Breaker activo
-   Documentación Swagger
