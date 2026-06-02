# ARCHITECTURE.md

## Visão Geral

Sistema responsável pela distribuição inteligente de leads para vendedores utilizando regras de negócio configuráveis.

O objetivo é receber leads de múltiplas origens, calcular um score de distribuição e direcionar o lead para o vendedor mais adequado.

Arquitetura inicialmente monolítica modular utilizando Quarkus, permitindo futura migração para microsserviços.

---

# Stack Tecnológica

## Backend

* Java 21
* Quarkus 3.x
* Hibernate ORM Panache
* JWT Authentication
* REST API
* Bean Validation

## Banco de Dados

* PostgreSQL

## Cache

* Redis

## Mensageria (Fase 2)

* AWS SQS

## Infraestrutura

* Docker
* Docker Compose
* AWS

---

# Arquitetura de Camadas

Cada módulo deve seguir:

```text
module
├── resource
├── service
├── repository
├── entity
├── dto
└── mapper
```

Responsabilidades:

Resource:

* Exposição da API REST

Service:

* Regras de negócio

Repository:

* Persistência

Entity:

* Modelagem do banco

DTO:

* Contratos da API

Mapper:

* Conversão Entity ↔ DTO

---

# Módulos

## Auth Module

Responsável por:

* Login
* JWT
* Controle de acesso
* Perfis de usuário

Entidades:

* User
* Role

---

## Lead Module

Responsável por:

* Cadastro de leads
* Atualização de status
* Histórico

Entidades:

* Lead
* LeadHistory

---

## Seller Module

Responsável por:

* Cadastro de vendedores
* Disponibilidade
* Performance

Entidades:

* Seller
* SellerPerformance

---

## Matchmaking Module

Responsável por:

* Distribuição dos leads
* Cálculo de score
* Regras de priorização

Entidades:

* Match
* MatchRule

---

## Notification Module

Responsável por:

* E-mail
* Push
* Webhook

Entidades:

* Notification
* NotificationLog

---

# Fluxo Principal

1. Lead recebido
2. Lead validado
3. Lead salvo
4. Matchmaking executado
5. Melhor vendedor identificado
6. Lead distribuído
7. Notificação enviada
8. Histórico registrado

---

# Estrutura de Pacotes

```text
src/main/java/com/company/leaddistribution

├── auth
├── lead
├── seller
├── matchmaking
├── notification
├── common
│
├── config
├── exception
├── security
└── util
```

---

# Segurança

Autenticação:

* JWT

Autorização:

* ADMIN
* MANAGER
* SELLER

Endpoints protegidos com:

```java
@RolesAllowed
```

---

# Banco de Dados

Principais tabelas:

users
roles
leads
lead_history
sellers
seller_performance
matches
match_rules
notifications
notification_logs

---

# Padrões Arquiteturais

* Clean Architecture (adaptada)
* Repository Pattern
* Service Layer Pattern
* DTO Pattern
* Dependency Injection
* Domain Driven Design Light

---

# Regras Técnicas

* Não acessar Repository diretamente pelo Resource
* Não retornar Entity na API
* Toda entrada deve utilizar DTO
* Toda saída deve utilizar DTO
* Todas as regras de negócio devem ficar em Service
* Todas as exceções devem ser tratadas globalmente

---

# Observabilidade

Implementar:

* Health Check
* Metrics
* Structured Logs

Endpoints:

/q/health
/q/metrics

---

# Evolução Futura

Fase 1:

* Monólito modular

Fase 2:

* Redis
* SQS
* Notificações assíncronas

Fase 3:

* Microsserviços
* Kubernetes
* Event Driven Architecture

---

Fim do documento.
