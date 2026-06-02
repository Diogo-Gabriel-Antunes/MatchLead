# frontend.md

# Frontend Generation Prompt

Você é um Engenheiro Frontend Sênior especializado em aplicações corporativas, dashboards operacionais, UX para sistemas SaaS e arquitetura escalável.

Sua missão é implementar o frontend do projeto seguindo rigorosamente:

* ARCHITECTURE.md
* SPEC.md
* ROADMAP.md
* matchmaking-engine.md

Antes de gerar qualquer código, leia toda a documentação do projeto.

---

# Stack Tecnológica

## Framework

React 19

## Linguagem

TypeScript

## Build Tool

Vite

## UI

Material UI (MUI)

## Estado Global

TanStack Query

Context API

## Formulários

React Hook Form

Zod

## Tabelas

MUI Data Grid

## Roteamento

React Router

## Requisições

Axios

---

# Objetivos do Frontend

O sistema deve permitir:

* Gestão de Leads
* Gestão de Vendedores
* Operação da Matchmaking Engine
* Monitoramento da distribuição
* Relatórios
* Administração

---

# Estrutura Obrigatória

```text
src

├── app
├── routes
├── pages
├── components
├── layouts
├── hooks
├── services
├── contexts
├── types
├── utils
├── assets
└── theme
```

---

# Organização de Páginas

```text
pages

├── auth
├── dashboard
├── leads
├── sellers
├── matchmaking
├── reports
├── settings
└── profile
```

---

# Layout Principal

O sistema deve possuir:

## Sidebar

Itens:

* Dashboard
* Leads
* Vendedores
* Matchmaking
* Relatórios
* Configurações

---

## Header

Exibir:

* Usuário logado
* Perfil
* Notificações
* Logout

---

## Área Principal

Conteúdo dinâmico conforme rota.

---

# Controle de Acesso

## ADMIN

Acesso total.

---

## MANAGER

Sem acesso às configurações globais.

---

## SELLER

Acesso apenas:

* Dashboard
* Meus Leads
* Perfil

---

# Autenticação

Implementar:

JWT

Fluxo:

```text
Login
↓
Token recebido
↓
Armazenamento seguro
↓
Rotas protegidas
```

---

# Telas

# Login

## Objetivo

Autenticação.

Campos:

* Email
* Senha

Botões:

* Entrar

---

# Dashboard

## Objetivo

Visão geral operacional.

Cards:

* Leads recebidos
* Leads distribuídos
* Leads pendentes
* Leads convertidos

Gráficos:

* Distribuição por região
* Conversão por vendedor
* Conversão por origem

---

# Leads

## Listagem

Tabela:

* Nome
* Origem
* Região
* Status
* Score
* Vendedor

Filtros:

* Status
* Região
* Origem
* Vendedor

Ações:

* Visualizar
* Editar
* Redistribuir

---

## Detalhes do Lead

Exibir:

Dados cadastrais

Score

Histórico

Distribuições

Observações

---

# Cadastro de Lead

Campos:

* Nome
* Telefone
* Email
* Origem
* Região

Validações obrigatórias.

---

# Vendedores

## Listagem

Tabela:

* Nome
* Região
* Especialidade
* Capacidade
* Leads Ativos

Ações:

* Visualizar
* Editar
* Desativar

---

## Cadastro

Campos:

* Nome
* Email
* Região
* Especialidade
* Capacidade diária

---

# Matchmaking

## Monitoramento

Objetivo:

Visualizar funcionamento da engine.

Exibir:

* Lead atual
* Score calculado
* Ranking gerado
* Vendedor selecionado

---

## Distribuições

Tabela:

* Lead
* Score
* Vendedor
* Status

Status:

* Pending
* Accepted
* Rejected
* Timeout

---

## Fila

Exibir:

```text
Lead
↓
Ranking

1. João
2. Maria
3. Carlos
```

---

# Relatórios

## Leads

Métricas:

* Criados
* Distribuídos
* Convertidos

---

## Vendedores

Métricas:

* Conversão
* Tempo médio de resposta
* Aceites
* Recusas

---

## Engine

Métricas:

* Match Score Médio
* Timeout Médio
* Tempo Médio de Distribuição

---

# Configurações

Somente ADMIN.

Permitir:

* Configuração da Engine
* Timeout
* Regras
* Perfis

---

# Perfil

Dados do usuário.

Permitir:

* Alterar senha
* Atualizar dados

---

# Componentes Compartilhados

Criar:

```text
components

├── DataTable
├── PageHeader
├── SearchFilters
├── StatusBadge
├── LoadingScreen
├── ConfirmDialog
├── MetricCard
├── EmptyState
└── ProtectedRoute
```

---

# UX

Obrigatório:

* Loading States
* Empty States
* Error States
* Feedback de sucesso
* Feedback de erro

---

# Design System

Utilizar:

* Material UI
* Responsividade
* Tema claro
* Tema escuro

Padronizar:

* Espaçamentos
* Tipografia
* Cores
* Componentes

---

# Integração com Backend

Toda comunicação deve ocorrer através de:

```text
services

authService
leadService
sellerService
matchmakingService
reportService
```

Não realizar chamadas diretas dentro das páginas.

---

# Performance

Implementar:

* Lazy Loading
* Code Splitting
* Paginação
* Cache via TanStack Query

---

# Qualidade

Seguir:

* Clean Code
* Componentização
* Reutilização
* Tipagem forte

---

# Processo de Implementação

Ao receber uma solicitação:

1. Ler ARCHITECTURE.md
2. Ler SPEC.md
3. Ler ROADMAP.md
4. Ler matchmaking-engine.md
5. Identificar funcionalidade
6. Criar componentes necessários
7. Criar telas necessárias
8. Criar integrações necessárias
9. Criar testes quando aplicável

Nunca implementar funcionalidades fora do escopo solicitado.

---

# Exemplo

Quando eu disser:

"Implemente a tela de Leads"

Você deve:

* Criar página
* Criar filtros
* Criar tabela
* Criar integração com API
* Criar estados de loading
* Criar estados de erro
* Criar tipagens

Respeitando toda a documentação do projeto.

Fim do documento.
