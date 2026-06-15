# contas-mensais

API REST para gerenciamento de contas mensais, construída com Spring Boot 3 e organizada seguindo os princípios de DDD (Domain-Driven Design).

## Stack

- Java 21
- Spring Boot 3.5.0
- Spring Data JPA + Hibernate
- Spring Security
- PostgreSQL 15
- Flyway
- Gradle
- Lombok

## Arquitetura

O projeto combina **DDD Bounded Contexts** com **Clean Architecture**, separado em quatro camadas de pacotes:

```
com.raphael.contasmensais/
  entity/               modelo de domínio puro (entidades, interfaces de repositório)
    financeiro/
      entity/           BaseEntity, Category, Transaction
      repository/
        category/       CategoryRepository (interface)
        transaction/    TransactionRepository (interface)

  service/              casos de uso — orquestra o domínio, sem conhecimento de HTTP ou JPA
    financeiro/
      category/         CategoryService
      transaction/      TransactionService

  api/                  controllers REST, DTOs (records) e tratamento de erros
    financeiro/
      category/         CategoryController, CategoryRequest, CategoryResponse
      transaction/      TransactionController, TransactionRequest, TransactionResponse
    exception/          GlobalExceptionHandler

  infrastructure/       implementações JPA e configurações técnicas
    config/             SecurityConfig
    financeiro/
      repository/
        category/       CategoryJpaRepository (package-private), CategoryRepositoryImpl
        transaction/    TransactionJpaRepository (package-private), TransactionRepositoryImpl
```

Fluxo de dependências: `api` → `service` → `entity` ← `infrastructure`

Cada entidade nova ganha uma subpasta própria dentro de `repository/` e `service/` em ambas as camadas.

### Unit of Work

Implementado via `@Transactional` do Spring. Métodos de escrita anotados com `@Transactional` garantem que todas as operações aconteçam em uma única transação de banco — se qualquer passo falhar, tudo faz rollback. Chamadas entre services dentro de um mesmo contexto transacional participam da mesma transação (propagação `REQUIRED` por padrão).

Exemplo: `TransactionService.create()` pode criar a categoria e a transação juntas — ou ambas são salvas, ou nenhuma.

### Validação

Aplicada em duas camadas:
- **Controllers** — `@Valid` nos `@RequestBody`: valida formato e presença dos campos do DTO antes de chegar ao service
- **Services** — `@Validated` + `@Valid` nos parâmetros: garante invariantes de negócio independente de quem chama o service

### Contextos atuais

| Contexto | Responsabilidade |
|---|---|
| `financeiro` | Transações e categorias financeiras |

Novos contextos (ex: `usuario`) seguem o mesmo padrão nas quatro camadas.

## Endpoints

### Categories

| Método | Path | Descrição |
|---|---|---|
| GET | `/categories` | Lista todas as categorias |
| GET | `/categories/{id}` | Busca categoria por ID |
| POST | `/categories` | Cria categoria |
| PUT | `/categories/{id}` | Atualiza categoria |
| DELETE | `/categories/{id}` | Remove categoria |

### Transactions

| Método | Path | Descrição |
|---|---|---|
| GET | `/transactions?userId={id}` | Lista transações por usuário |
| GET | `/transactions/{id}` | Busca transação por ID |
| POST | `/transactions` | Cria transação (categoria nova ou existente) |
| PUT | `/transactions/{id}` | Atualiza transação |
| DELETE | `/transactions/{id}` | Remove transação |

Ao criar ou atualizar uma transação, se `category.id` for `null` uma nova categoria é criada junto; se tiver valor, a categoria existente é validada e associada.

## Migrações de banco

Gerenciadas pelo **Flyway**. Os scripts ficam em `src/main/resources/db/migration/` com a nomenclatura `V{n}__{descricao}.sql`. O Hibernate está configurado com `ddl-auto=validate` — ele apenas valida o schema, nunca o modifica.

## Testes

A estrutura de testes espelha a estrutura de `src/main`:

```
src/test/
  service/financeiro/
    category/           CategoryServiceTest
    transaction/        TransactionServiceTest
  infrastructure/financeiro/repository/
    category/           CategoryRepositoryImplTest
    transaction/        TransactionRepositoryImplTest
```

| Camada | Estratégia | Ferramentas |
|---|---|---|
| Services | Teste unitário, sem contexto Spring | JUnit 5 + Mockito |
| Repositories | Teste de integração com banco em memória | JUnit 5 + `@DataJpaTest` + H2 |

**Services** usam `@ExtendWith(MockitoExtension.class)`: dependências são mockadas com `@Mock`, o serviço instanciado com `@InjectMocks`. Sem proxy Spring, `@Valid`/`@Validated` não é aplicado — apenas a lógica de negócio é testada.

**Repositories** usam `@DataJpaTest`: Spring sobe apenas a camada JPA com banco H2 em memória. O Flyway é substituído por `ddl-auto=create-drop`. Os `*RepositoryImpl` precisam ser importados explicitamente via `@Import` pois não são Spring Data JPA interfaces e não são carregados automaticamente pelo slice.

```bash
# Rodar todos os testes
./gradlew test

# Rodar uma classe específica
./gradlew test --tests "com.raphael.contasmensais.service.financeiro.category.CategoryServiceTest"

# Forçar reexecução mesmo sem mudanças
./gradlew test --rerun-tasks
```

## CI

GitHub Actions configurado em `.github/workflows/ci.yml`. Roda em todo push e pode ser disparado manualmente pela aba Actions do GitHub.

Passos: checkout → Java 21 (Temurin) → cache Gradle → build → testes → relatório no summary.

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
