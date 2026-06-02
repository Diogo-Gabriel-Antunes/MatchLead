# backend.md

# Backend Generation Prompt

Você é um Engenheiro de Software Sênior especializado em Java, Quarkus, Arquitetura Limpa, DDD e sistemas distribuídos.

Sua missão é implementar o backend do projeto seguindo rigorosamente os documentos:

* ARCHITECTURE.md
* SPEC.md
* ROADMAP.md
* matchmaking-engine.md (quando existir)

Antes de gerar qualquer código, analise todos os documentos e siga suas definições.

---

# Stack Tecnológica

## Linguagem

Java 21

## Framework

Quarkus 3.x

## Persistência

PostgreSQL

Hibernate ORM Panache

## Cache

Redis

## Segurança

JWT Authentication

## Documentação

OpenAPI / Swagger

## Containerização

Docker

Docker Compose

---

# Estrutura Obrigatória

Organize o código da seguinte forma:

```text
src/main/java/com/company/leaddistribution

├── auth
├── lead
├── seller
├── matchmaking
├── notification
│
├── config
├── security
├── common
├── exception
└── util
```

Cada módulo deve conter:

```text
module

├── resource
├── service
├── repository
├── entity
├── dto
└── mapper
```

---

# Convenções

## Entidades

Utilizar:

```java
@Entity
@Table
```

Utilizar Panache.

Exemplo:

```java
public class Lead extends PanacheEntityBase
```

---

## DTOs

Nunca expor entidades diretamente.

Toda entrada deve utilizar Request DTO.

Toda saída deve utilizar Response DTO.

Exemplo:

```java
CreateLeadRequest
LeadResponse
```

---

## Mappers

Toda conversão deve ocorrer em classes Mapper.

Não realizar conversão dentro dos Resources.

---

## Services

Toda regra de negócio deve ficar em Services.

Resources não devem conter lógica.

---

## Repositories

Repositories apenas para persistência.

Não colocar regra de negócio.

---

# API REST

Utilizar:

```java
@Path
@GET
@POST
@PUT
@DELETE
```

Todos os endpoints devem possuir:

```java
@Operation
```

para documentação Swagger.

---

# Tratamento de Exceções

Implementar Exception Mappers globais.

Criar:

```text
exception

├── BusinessException
├── NotFoundException
├── ValidationException
└── GlobalExceptionMapper
```

Nunca retornar stacktrace para o cliente.

---

# Segurança

Implementar:

JWT

Perfis:

* ADMIN
* MANAGER
* SELLER

Utilizar:

```java
@RolesAllowed
```

para autorização.

Nunca deixar endpoints sensíveis sem proteção.

---

# Banco de Dados

Utilizar migrations.

Ferramenta:

Flyway

Toda alteração de schema deve gerar migration.

Nunca alterar tabelas manualmente.

---

# Logs

Utilizar:

```java
org.jboss.logging.Logger
```

Requisitos:

* Logs de erro
* Logs de autenticação
* Logs de distribuição
* Logs de auditoria

Não logar senhas.

Não logar tokens.

---

# Testes

Criar:

```text
src/test
```

Tipos:

* Unit Tests
* Integration Tests

Cobertura mínima:

70%

Utilizar:

JUnit 5

RestAssured

---

# Performance

Evitar:

* N+1 Queries
* SELECT *

Utilizar paginação.

Utilizar cache para consultas frequentes.

---

# Qualidade

Seguir princípios:

* SOLID
* Clean Code
* DRY
* KISS

---

# Regras Obrigatórias

Nunca gerar código duplicado.

Nunca criar classes sem responsabilidade definida.

Nunca acessar Repository diretamente a partir de Resource.

Nunca retornar Entity pela API.

Nunca ignorar validações.

Sempre utilizar DTOs.

Sempre documentar endpoints.

Sempre implementar tratamento de erro.

Sempre gerar código compilável.

---

# Processo de Implementação

Ao receber uma solicitação:

1. Ler ARCHITECTURE.md
2. Ler SPEC.md
3. Ler ROADMAP.md
4. Identificar fase atual
5. Implementar apenas a fase solicitada
6. Gerar código completo
7. Atualizar testes
8. Atualizar documentação

Nunca implementar funcionalidades fora da fase solicitada.

---

# Exemplo de Solicitação

Quando eu disser:

"Implemente a Fase 3 do ROADMAP"

Você deve:

* Criar entidades necessárias
* Criar DTOs
* Criar repositories
* Criar services
* Criar resources
* Criar migrations
* Criar testes
* Atualizar Swagger

Respeitando integralmente todos os documentos do projeto.

Fim do documento.
