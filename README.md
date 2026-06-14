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

O projeto combina **DDD Bounded Contexts** com **Clean Architecture**, separado em quatro camadas de pacotes:

```
com.raphael.contasmensais/
  domain/               modelo de domínio puro (entidades, interfaces de repositório)
    financeiro/
      entity/           Category, Transaction, BaseEntity
      repository/
        category/       CategoryRepository (interface)
        transaction/    TransactionRepository (interface)

  service/              casos de uso — orquestra o domínio, sem conhecimento de HTTP ou JPA
    financeiro/

  api/                  controllers REST e DTOs
    financeiro/

  infrastructure/       implementações JPA dos repositórios
    financeiro/
      repository/
        category/       CategoryJpaRepository, CategoryRepositoryImpl
        transaction/    TransactionJpaRepository, TransactionRepositoryImpl
```

Fluxo de dependências: `api` → `service` → `domain` ← `infrastructure`

Cada entidade nova ganha uma subpasta própria dentro de `repository/` e `service/` em ambas as camadas.

### Unit of Work

Implementado via `@Transactional` do Spring. Métodos de escrita anotados com `@Transactional` garantem que todas as operações aconteçam em uma única transação de banco — se qualquer passo falhar, tudo faz rollback. Chamadas entre services dentro de um mesmo contexto transacional participam da mesma transação (propagação `REQUIRED` por padrão).

Exemplo: `TransactionService.createWithNewCategory()` cria a categoria e a transação juntas — ou ambas são salvas, ou nenhuma.

### Contextos atuais

| Contexto | Responsabilidade |
|---|---|
| `financeiro` | Transações e categorias financeiras |

Novos contextos (ex: `usuario`) seguem o mesmo padrão nas quatro camadas.

## Migrações de banco

Gerenciadas pelo **Flyway**. Os scripts ficam em `src/main/resources/db/migration/` com a nomenclatura `V{n}__{descricao}.sql`. O Hibernate está configurado com `ddl-auto=validate` — ele apenas valida o schema, nunca o modifica.

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
