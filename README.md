# transparency-service

## 1. Descripción

Microservicio de consulta encargado de exponer la trazabilidad y registros de transparencia de una elección (actas, validaciones, eventos).

Forma parte del lado de consulta (CQRS) y utiliza Redis para cachear resultados.

---

## 2. Tecnologías

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- PostgreSQL
- Redis
- Flyway
- Lombok
- Swagger (Springdoc)

---

## 3. Arquitectura

Capas:

- Controller → endpoints REST
- Service → lógica + cache (cache-aside)
- Repository → acceso a DB
- Mapper → transformación entidad → DTO
- Cache Adapter → Redis (JSON)

---

## 4. Endpoint

```
GET /api/v1/transparency?electionId=1
```

---

## 5. Respuesta

```json
{
  "electionId": 1,
  "records": [
    {
      "eventType": "ACTA_REGISTRADA",
      "description": "Acta subida desde mesa 101",
      "timestamp": "2026-01-01T10:00:00"
    }
  ]
}
```

---

## 6. Variables (.env)

```
DB_URL=jdbc:postgresql://localhost:5433/transparency_db
DB_USER=transparency_user
DB_PASSWORD=123456

REDIS_HOST=localhost
REDIS_PORT=6380

PORT=8084
```

---

## 7. Base de datos

```sql
CREATE DATABASE transparency_db;

CREATE USER transparency_user WITH PASSWORD '123456';

GRANT ALL PRIVILEGES ON DATABASE transparency_db TO transparency_user;

\c transparency_db
GRANT ALL ON SCHEMA public TO transparency_user;
```

---

## 8. Migraciones (Flyway)

Ubicación:

```
src/main/resources/db/migration
```

### V1__init.sql

```sql
CREATE TABLE transparency_record (
    id SERIAL PRIMARY KEY,
    election_id BIGINT,
    event_type VARCHAR(100),
    description TEXT,
    timestamp TIMESTAMP
);
```

### V2__seed.sql

```sql
INSERT INTO transparency_record (election_id, event_type, description, timestamp) VALUES
(1, 'ACTA_REGISTRADA', 'Acta subida desde mesa 101', NOW()),
(1, 'RESULTADO_VALIDADO', 'Validación completada', NOW());
```

---

## 9. Ejecución

```bash
export $(grep -v '^#' .env | xargs)
mvn spring-boot:run
```

---

## 10. Cache (Redis)

Clave:

```
transparency:{electionId}
```

Estrategia:

1. Busca en cache
2. Si no existe → DB
3. Guarda en Redis (JSON)

---

## 11. Manejo de errores

### 404

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "No records found"
}
```

---

## 12. Estado

Microservicio completo y funcional:

- Cache distribuido con Redis
- Migraciones automáticas con Flyway
- Arquitectura limpia (Controller-Service-Repository-Mapper)
- Manejo de errores centralizado
