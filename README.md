# contas-mensais

API REST para gerenciamento de contas mensais, construída com Spring Boot 3 e organizada seguindo os princípios de DDD (Domain-Driven Design).

## Stack

- Java 21
- Spring Boot 3.5.0
- Spring Data JPA + Hibernate
- Spring Security
- PostgreSQL 15
- Gradle
- Lombok

## Arquitetura

O projeto usa **Bounded Contexts** do DDD separados em três camadas de pacotes:

```
com.raphael.contasmensais/
  domain/               modelo de domínio puro
    financeiro/         contexto: financeiro
      entity/           entidades JPA

  api/                  controllers REST e DTOs
    financeiro/

  infrastructure/       repositórios JPA e configurações de persistência
    financeiro/
```

### Contextos atuais

| Contexto | Responsabilidade |
|---|---|
| `financeiro` | Transações e categorias financeiras |

Novos contextos (ex: `usuario`) seguem o mesmo padrão nas três camadas.

## Como rodar

### Com Docker (recomendado)

Sobe a API e o banco PostgreSQL:

```bash
docker compose up
```

A API ficará disponível em `http://localhost:8080`.

### Localmente

Requer Java 21 e PostgreSQL rodando. Configure `src/main/resources/application.properties` com a URL do banco e rode:

```bash
./gradlew bootRun
```

## Hot reload

Com o Docker, o projeto é montado como volume. O VS Code com a extensão Java compila em background — qualquer alteração no código reinicia a aplicação automaticamente via Spring Boot DevTools.
