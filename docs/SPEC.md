# SPEC.md

# Sistema de Distribuição Inteligente de Leads

Versão: 1.0

---

# 1. Visão Geral

O sistema tem como objetivo receber leads provenientes de múltiplas fontes, analisar suas características, executar uma engine de matchmaking e distribuir automaticamente cada lead para o vendedor mais adequado.

A distribuição deve considerar:

* Perfil do lead
* Intenção de compra
* Histórico de interações
* Disponibilidade do vendedor
* Especialidade do vendedor
* Região atendida
* Capacidade operacional

O sistema deve permitir rastreabilidade completa do ciclo de vida do lead.

---

# 2. Objetivos do Produto

## Objetivos Principais

* Automatizar a distribuição de leads
* Reduzir tempo de resposta comercial
* Melhorar taxa de conversão
* Equilibrar carga entre vendedores
* Garantir rastreabilidade

## Objetivos Secundários

* Disponibilizar métricas operacionais
* Permitir redistribuição manual
* Permitir evolução para múltiplos algoritmos de matchmaking

---

# 3. Perfis de Usuário

## ADMIN

Permissões:

* Gerenciar usuários
* Gerenciar vendedores
* Configurar regras
* Visualizar todos os leads
* Visualizar relatórios
* Redistribuir leads

---

## MANAGER

Permissões:

* Gerenciar equipe
* Visualizar leads da equipe
* Redistribuir leads
* Visualizar indicadores

---

## SELLER

Permissões:

* Visualizar leads atribuídos
* Atualizar status
* Registrar observações
* Aceitar ou recusar leads

---

# 4. Fluxo Principal

## Fluxo de Distribuição

1. Lead capturado
2. Lead validado
3. Lead enriquecido
4. Engine executada
5. Ranking de vendedores gerado
6. Primeiro vendedor notificado
7. Vendedor aceita ou recusa
8. Lead atribuído
9. Histórico registrado
10. Métricas atualizadas

---

# 5. Casos de Uso

## UC001 - Login

Descrição:

Permitir autenticação de usuários.

Resultado:

JWT válido retornado.

---

## UC002 - Cadastro de Lead

Descrição:

Cadastrar novo lead.

Campos obrigatórios:

* Nome
* Telefone ou E-mail
* Origem

Resultado:

Lead criado.

---

## UC003 - Consulta de Leads

Descrição:

Permitir busca paginada.

Filtros:

* Status
* Vendedor
* Data
* Origem
* Região

---

## UC004 - Atualização de Lead

Descrição:

Atualizar informações cadastrais.

---

## UC005 - Alteração de Status

Status possíveis:

* NEW
* ASSIGNED
* CONTACTED
* QUALIFIED
* PROPOSAL
* WON
* LOST

---

## UC006 - Cadastro de Vendedor

Descrição:

Cadastrar vendedor apto para receber leads.

---

## UC007 - Aceite de Lead

Descrição:

Permitir que o vendedor aceite um lead.

Resultado:

Lead atribuído ao vendedor.

---

## UC008 - Recusa de Lead

Descrição:

Permitir que o vendedor recuse um lead.

Resultado:

Engine deve selecionar próximo vendedor.

---

## UC009 - Redistribuição Manual

Descrição:

Transferir lead entre vendedores.

Perfis autorizados:

* ADMIN
* MANAGER

---

## UC010 - Histórico

Descrição:

Registrar todas as alterações relevantes.

---

## UC011 - Notificações

Descrição:

Enviar notificações operacionais.

Eventos:

* Novo lead
* Redistribuição
* Aceite
* Recusa
* Conversão

---

# 6. Matchmaking

## Objetivo

Encontrar automaticamente o vendedor mais adequado para cada lead.

## Entradas

Lead:

* Dados cadastrais
* Origem
* Região
* Perfil

Vendedor:

* Região atendida
* Especialidade
* Disponibilidade
* Capacidade
* Performance

## Saída

Lista ordenada de vendedores candidatos.

Observação:

A lógica detalhada será documentada em matchmaking-engine.md.

---

# 7. Regras de Negócio

## RN001

Todo lead deve possuir:

* telefone ou
* e-mail

---

## RN002

Não permitir leads duplicados.

Critérios:

* Mesmo telefone
  ou
* Mesmo e-mail

---

## RN003

Somente vendedores ativos podem receber leads.

---

## RN004

Vendedor não pode ultrapassar capacidade diária.

---

## RN005

Toda alteração deve gerar histórico.

---

## RN006

SELLER não pode visualizar leads de outros vendedores.

---

## RN007

Somente ADMIN e MANAGER podem redistribuir leads.

---

## RN008

Lead recusado deve retornar para a engine.

---

## RN009

Lead sem vendedor elegível deve permanecer em fila.

---

# 8. Entidades de Negócio

## User

Campos:

* id
* name
* email
* password
* role
* active
* createdAt

---

## Seller

Campos:

* id
* name
* email
* active
* region
* specialization
* dailyCapacity
* createdAt

---

## Lead

Campos:

* id
* name
* email
* phone
* source
* region
* status
* createdAt
* updatedAt

---

## Assignment

Representa a atribuição do lead.

Campos:

* id
* leadId
* sellerId
* assignedAt

---

## LeadHistory

Campos:

* id
* leadId
* action
* previousValue
* newValue
* createdAt

---

## Notification

Campos:

* id
* recipient
* type
* status
* createdAt

---

# 9. API REST

## Auth

POST /auth/login

---

## Leads

GET /leads

GET /leads/{id}

POST /leads

PUT /leads/{id}

PATCH /leads/{id}/status

---

## Sellers

GET /sellers

GET /sellers/{id}

POST /sellers

PUT /sellers/{id}

DELETE /sellers/{id}

---

## Matchmaking

POST /matchmaking/execute/{leadId}

POST /matchmaking/reassign

POST /matchmaking/accept

POST /matchmaking/reject

---

## Reports

GET /reports/leads

GET /reports/conversions

GET /reports/sellers

---

# 10. Auditoria

O sistema deve registrar:

* Login
* Criação de lead
* Atualização de lead
* Distribuição
* Aceite
* Recusa
* Alteração de status
* Redistribuição

---

# 11. Requisitos Não Funcionais

* API REST JSON
* JWT Authentication
* PostgreSQL
* Redis
* Docker Compose
* OpenAPI
* Health Checks
* Logs estruturados
* Cobertura mínima de testes de 70%

---

# 12. Critérios de Aceitação do MVP

O MVP será considerado concluído quando:

* Login estiver funcionando
* JWT estiver funcionando
* CRUD de Leads estiver funcionando
* CRUD de Sellers estiver funcionando
* Matchmaking básico estiver funcionando
* Aceite e recusa estiverem funcionando
* Histórico estiver funcionando
* Swagger estiver funcionando
* Docker Compose estiver funcionando

Fim do documento.
