# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run locally (requires Java 21)
./gradlew bootRun

# Run via Docker (API + PostgreSQL)
docker compose up

# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.raphael.contasmensais.SomeTest"
```

## Stack

- **Java 21** / Spring Boot 3.5.0
- **Gradle** (use `./gradlew`, not a global gradle)
- **Spring Data JPA** + **Hibernate** over **PostgreSQL 15**
- **Spring Security**
- **Lombok** — use `@Data`, `@Builder`, `@Slf4j` etc. to avoid boilerplate; avoid writing getters/setters manually

## Architecture

Package root: `com.raphael.contasmensais`

The project follows DDD Bounded Contexts split into three top-level packages:

```
domain/               pure domain model, no framework dependencies
  financeiro/         bounded context: financial
    entity/           JPA entities (Category, Transaction, BaseEntity)

api/                  REST layer (controllers, DTOs, request/response mapping)
  financeiro/

infrastructure/       persistence implementations (JPA repositories, configs)
  financeiro/
```

Each bounded context lives in all three layers under the same context name (`financeiro`). New contexts (e.g. `usuario`) follow the same pattern.

`BaseEntity` (in `domain/financeiro/entity`) is a `@MappedSuperclass` providing `createdAt` and `updatedAt` via JPA Auditing — all entities extend it. `@EnableJpaAuditing` is on `ContasMensaisApplication`.

## Database

In Docker: the `db` service is `postgres:15-alpine`, database name `contasmensais`.

`spring.jpa.hibernate.ddl-auto=update` is active — schema is auto-managed by Hibernate in development. For production, switch to `validate` and use migration scripts.

## Hot Reload (Docker)

The `api` service mounts the project as a volume and runs `./gradlew bootRun`. Spring Boot DevTools watches `build/classes/` for changes. The VS Code Java extension compiles in the background automatically — saving a file triggers recompile → DevTools restarts the app.

Gradle dependency cache is persisted in the `gradle_cache` Docker volume; subsequent starts are fast.
