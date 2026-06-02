# ROADMAP.md

# Roadmap de Implementação

Objetivo: implementar o sistema de forma incremental, garantindo que cada fase produza uma aplicação funcional e testável.

---

# Fase 0 - Setup Inicial

Objetivo:

Criar a fundação técnica do projeto.

Entregáveis:

* Projeto Quarkus 3.x
* Java 21
* Docker Compose
* PostgreSQL
* Redis
* OpenAPI
* Health Check
* Estrutura modular
* Configuração de ambientes

Critério de conclusão:

Aplicação sobe localmente utilizando Docker.

---

# Fase 1 - Segurança e Autenticação

Objetivo:

Implementar autenticação e autorização.

Entregáveis:

* User Entity
* Role Entity
* JWT
* Login Endpoint
* Controle de Perfis
* Seed Admin

Endpoints:

POST /auth/login

Critério de conclusão:

Usuário autenticado consegue acessar rotas protegidas.

---

# Fase 2 - Gestão de Vendedores

Objetivo:

Cadastrar e administrar vendedores.

Entregáveis:

* Seller Entity
* CRUD Seller
* Especialidades
* Regiões
* Capacidade diária
* Status ativo/inativo

Endpoints:

GET /sellers

GET /sellers/{id}

POST /sellers

PUT /sellers/{id}

DELETE /sellers/{id}

Critério de conclusão:

Cadastro completo de vendedores funcional.

---

# Fase 3 - Gestão de Leads

Objetivo:

Cadastrar e consultar leads.

Entregáveis:

* Lead Entity
* CRUD Lead
* Filtros
* Paginação
* Validações
* Prevenção de duplicidade

Endpoints:

GET /leads

GET /leads/{id}

POST /leads

PUT /leads/{id}

Critério de conclusão:

Leads podem ser criados e consultados.

---

# Fase 4 - Histórico e Auditoria

Objetivo:

Garantir rastreabilidade.

Entregáveis:

* LeadHistory
* Audit Logs
* Registro de eventos

Eventos monitorados:

* Criação
* Atualização
* Distribuição
* Aceite
* Recusa

Critério de conclusão:

Todas as ações relevantes são auditadas.

---

# Fase 5 - Matchmaking Engine Base

Objetivo:

Criar primeira versão da engine.

Entregáveis:

* Seleção de vendedores elegíveis
* Filtro por região
* Filtro por especialidade
* Filtro por disponibilidade
* Ranking inicial

Endpoints:

POST /matchmaking/execute/{leadId}

Critério de conclusão:

Sistema consegue selecionar vendedores candidatos.

---

# Fase 6 - Distribuição de Leads

Objetivo:

Atribuir leads automaticamente.

Entregáveis:

* Assignment Entity
* Associação Lead → Seller
* Registro histórico
* Atualização de status

Critério de conclusão:

Lead distribuído automaticamente.

---

# Fase 7 - Aceite e Recusa

Objetivo:

Implementar fluxo operacional.

Entregáveis:

* Aceite de lead
* Recusa de lead
* Timeout
* Reprocessamento

Endpoints:

POST /matchmaking/accept

POST /matchmaking/reject

Critério de conclusão:

Fluxo completo de aceite funcionando.

---

# Fase 8 - Notificações

Objetivo:

Notificar vendedores.

Entregáveis:

* Notification Entity
* Serviço de notificações
* Templates

Canais:

* E-mail
* Webhook

Critério de conclusão:

Notificações enviadas com sucesso.

---

# Fase 9 - Matchmaking Engine Avançada

Objetivo:

Implementar inteligência de distribuição.

Entregáveis:

* Scoring
* Balanceamento de carga
* Ranking avançado
* Fallback automático
* Explicação do match

Documentação:

matchmaking-engine.md

Critério de conclusão:

Distribuição baseada em score.

---

# Fase 10 - Dashboard e Relatórios

Objetivo:

Disponibilizar métricas operacionais.

Entregáveis:

* Conversões
* Leads distribuídos
* Leads pendentes
* Performance dos vendedores

Endpoints:

GET /reports/leads

GET /reports/conversions

GET /reports/sellers

Critério de conclusão:

Métricas disponíveis via API.

---

# Fase 11 - Cache

Objetivo:

Melhorar performance.

Entregáveis:

* Redis
* Cache de consultas
* Cache de configurações

Critério de conclusão:

Consultas críticas utilizando cache.

---

# Fase 12 - Processamento Assíncrono

Objetivo:

Escalar o sistema.

Entregáveis:

* AWS SQS
* Filas de eventos
* Processamento assíncrono

Eventos:

* LeadCreated
* LeadAssigned
* LeadAccepted
* LeadRejected

Critério de conclusão:

Eventos desacoplados da API.

---

# Fase 13 - Produção

Objetivo:

Preparar ambiente produtivo.

Entregáveis:

* Docker
* CI/CD
* Observabilidade
* Logs estruturados
* Métricas
* Monitoramento

Critério de conclusão:

Sistema apto para produção.

---

# MVP

O MVP será considerado concluído ao finalizar a Fase 8.

Escopo MVP:

✅ Login

✅ JWT

✅ CRUD de Leads

✅ CRUD de Sellers

✅ Distribuição Básica

✅ Aceite

✅ Recusa

✅ Histórico

✅ Notificações

Fases 9 a 13 serão consideradas evolução do produto.

Fim do documento.
