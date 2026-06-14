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

The project follows DDD Bounded Contexts aligned with Clean Architecture, split into four top-level packages:

```
domain/               pure domain model, no framework dependencies
  financeiro/
    entity/           JPA entities (Category, Transaction, BaseEntity)
    repository/       domain repository interfaces — one subfolder per entity
      category/       CategoryRepository
      transaction/    TransactionRepository

service/              use cases / application logic — orchestrates domain, no HTTP/JPA knowledge
  financeiro/

api/                  REST layer (controllers, DTOs, request/response mapping)
  financeiro/

infrastructure/       persistence implementations
  financeiro/
    repository/       one subfolder per entity
      category/       CategoryJpaRepository (package-private), CategoryRepositoryImpl
      transaction/    TransactionJpaRepository (package-private), TransactionRepositoryImpl
```

Dependency direction: `api` → `service` → `domain` ← `infrastructure`. Each bounded context lives in all four layers under the same context name. New contexts (e.g. `usuario`) and new entities follow the same per-subfolder pattern.

Services are plain classes annotated with `@Service` (no interface). Cross-service dependencies are allowed — `TransactionService` depends on `CategoryService`. The Unit of Work is enforced via `@Transactional`: all operations within an annotated method participate in a single database transaction (default propagation = REQUIRED). Read-only methods use `@Transactional(readOnly = true)` for performance.

`BaseEntity` (in `domain/financeiro/entity`) is a `@MappedSuperclass` providing `createdAt` and `updatedAt` via JPA Auditing — all entities extend it. `@EnableJpaAuditing` is on `ContasMensaisApplication`.

The `*JpaRepository` interfaces are package-private — they are internal to the infrastructure package and only used by their paired `*RepositoryImpl`.

## Database

In Docker: the `db` service is `postgres:15-alpine`, database name `contasmensais`.

Schema is managed by **Flyway** (`db/migration/`). `ddl-auto=validate` — Hibernate only validates, never modifies the schema. Migration files follow the `V{n}__{description}.sql` naming convention.

## Hot Reload (Docker)

The `api` service mounts the project as a volume and runs `./gradlew bootRun`. Spring Boot DevTools watches `build/classes/` for changes. The VS Code Java extension compiles in the background automatically — saving a file triggers recompile → DevTools restarts the app.

Gradle dependency cache is persisted in the `gradle_cache` Docker volume; subsequent starts are fast.
