# API-CONTRACTS.md

# API Contracts

Versão: 1.0

Objetivo:

Definir os contratos oficiais da API REST.

Todos os serviços devem respeitar exatamente os formatos descritos neste documento.

---

# Convenções

## Base URL

```text
/api/v1
```

---

## Content Type

Request:

```http
Content-Type: application/json
```

Response:

```http
Content-Type: application/json
```

---

## Datas

Formato:

```text
2026-01-01T10:30:00Z
```

ISO-8601.

---

## Paginação

Request:

```http
?page=0&size=20
```

Response:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

---

# AUTH

## Login

### Request

```http
POST /auth/login
```

Body:

```json
{
  "email": "admin@company.com",
  "password": "123456"
}
```

### Response

```json
{
  "accessToken": "jwt-token",
  "expiresIn": 3600,
  "role": "ADMIN"
}
```

---

## Perfil Atual

### Request

```http
GET /auth/me
```

### Response

```json
{
  "id": 1,
  "name": "Administrador",
  "email": "admin@company.com",
  "role": "ADMIN"
}
```

---

# USERS

## Criar Usuário

### Request

```http
POST /users
```

```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "123456",
  "role": "SELLER"
}
```

### Response

```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "role": "SELLER",
  "active": true
}
```

---

# SELLERS

## Criar Vendedor

### Request

```http
POST /sellers
```

```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "region": "SC",
  "specialization": "AUTOMOTIVO",
  "dailyCapacity": 50,
  "active": true
}
```

### Response

```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "region": "SC",
  "specialization": "AUTOMOTIVO",
  "dailyCapacity": 50,
  "active": true
}
```

---

## Listar Vendedores

### Request

```http
GET /sellers
```

### Response

```json
{
  "content": [
    {
      "id": 1,
      "name": "João Silva",
      "region": "SC",
      "active": true
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## Detalhes do Vendedor

### Request

```http
GET /sellers/{id}
```

### Response

```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "region": "SC",
  "specialization": "AUTOMOTIVO",
  "dailyCapacity": 50,
  "active": true
}
```

---

## Atualizar Vendedor

### Request

```http
PUT /sellers/{id}
```

```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "region": "SC",
  "specialization": "AUTOMOTIVO",
  "dailyCapacity": 50,
  "active": true
}
```

### Response

```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "region": "SC",
  "specialization": "AUTOMOTIVO",
  "dailyCapacity": 50,
  "active": true
}
```

---

## Desativar Vendedor

### Request

```http
DELETE /sellers/{id}
```

### Response

```http
204 No Content
```

Observação:

O endpoint realiza inativação lógica, alterando `active` para `false`.

---

# LEADS

## Criar Lead

### Request

```http
POST /leads
```

```json
{
  "name": "Maria Souza",
  "email": "maria@email.com",
  "phone": "47999999999",
  "source": "FACEBOOK",
  "region": "SC"
}
```

### Response

```json
{
  "id": 1,
  "name": "Maria Souza",
  "email": "maria@email.com",
  "phone": "47999999999",
  "source": "FACEBOOK",
  "region": "SC",
  "status": "NEW",
  "seller": null,
  "createdAt": "2026-01-01T10:00:00Z",
  "updatedAt": "2026-01-01T10:00:00Z"
}
```

---

## Listar Leads

### Request

```http
GET /leads
```

Filtros:

```http
?status=NEW
&sellerId=1
&region=SC
&source=FACEBOOK
&page=0
&size=20
```

### Response

```json
{
  "content": [
    {
      "id": 1,
      "name": "Maria Souza",
      "source": "FACEBOOK",
      "region": "SC",
      "status": "ASSIGNED",
      "sellerId": 1,
      "createdAt": "2026-01-01T10:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## Detalhes do Lead

### Request

```http
GET /leads/{id}
```

### Response

```json
{
  "id": 1,
  "name": "Maria Souza",
  "email": "maria@email.com",
  "phone": "47999999999",
  "source": "FACEBOOK",
  "region": "SC",
  "status": "ASSIGNED",
  "seller": {
    "id": 1,
    "name": "João Silva"
  },
  "createdAt": "2026-01-01T10:00:00Z",
  "updatedAt": "2026-01-01T10:00:00Z"
}
```

---

## Atualizar Lead

### Request

```http
PUT /leads/{id}
```

```json
{
  "name": "Maria Souza",
  "email": "maria@email.com",
  "phone": "47999999999",
  "source": "GOOGLE",
  "region": "PR"
}
```

### Response

```json
{
  "id": 1,
  "name": "Maria Souza",
  "email": "maria@email.com",
  "phone": "47999999999",
  "source": "GOOGLE",
  "region": "PR",
  "status": "NEW",
  "seller": null,
  "createdAt": "2026-01-01T10:00:00Z",
  "updatedAt": "2026-01-01T10:05:00Z"
}
```

---

## Atualizar Status

### Request

```http
PATCH /leads/{id}/status
```

```json
{
  "status": "CONTACTED"
}
```

### Response

```json
{
  "success": true,
  "status": "CONTACTED"
}
```

---

# MATCHMAKING

## Executar Match

### Request

```http
POST /matchmaking/execute/{leadId}
```

### Response

```json
{
  "leadId": 1,
  "score": 87,
  "selectedSellerId": 1,
  "selectedSellerName": "João Silva"
}
```

---

## Aceitar Lead

### Request

```http
POST /matchmaking/accept
```

```json
{
  "leadId": 1,
  "sellerId": 1
}
```

### Response

```json
{
  "success": true,
  "status": "ACCEPTED"
}
```

---

## Recusar Lead

### Request

```http
POST /matchmaking/reject
```

```json
{
  "leadId": 1,
  "sellerId": 1,
  "reason": "Indisponível"
}
```

### Response

```json
{
  "success": true,
  "status": "REJECTED"
}
```

---

## Ranking Gerado

### Request

```http
GET /matchmaking/ranking/{leadId}
```

### Response

```json
{
  "leadId": 1,
  "score": 87,
  "ranking": [
    {
      "position": 1,
      "sellerId": 1,
      "sellerName": "João Silva",
      "rankingScore": 95
    },
    {
      "position": 2,
      "sellerId": 2,
      "sellerName": "Maria Oliveira",
      "rankingScore": 91
    }
  ]
}
```

---

# HISTÓRICO

## Histórico do Lead

### Request

```http
GET /leads/{id}/history
```

### Response

```json
{
  "leadId": 1,
  "events": [
    {
      "type": "LEAD_CREATED",
      "previousValue": null,
      "newValue": "Lead criado",
      "date": "2026-01-01T10:00:00Z"
    },
    {
      "type": "LEAD_STATUS_CHANGED",
      "previousValue": "NEW",
      "newValue": "CONTACTED",
      "date": "2026-01-01T10:02:00Z"
    }
  ]
}
```

Observação:

Os eventos são retornados do mais recente para o mais antigo.

Eventos possíveis:

```text
LEAD_CREATED
LEAD_UPDATED
LEAD_STATUS_CHANGED
LEAD_ASSIGNED
LEAD_ACCEPTED
LEAD_REJECTED
```

---

# DASHBOARD

## Resumo

### Request

```http
GET /dashboard/summary
```

### Response

```json
{
  "receivedLeads": 1000,
  "assignedLeads": 850,
  "convertedLeads": 210,
  "pendingLeads": 150
}
```

---

# REPORTS

## Conversão por Vendedor

### Request

```http
GET /reports/sellers
```

### Response

```json
[
  {
    "sellerId": 1,
    "sellerName": "João Silva",
    "conversionRate": 18.5,
    "acceptedLeads": 300,
    "wonLeads": 55
  }
]
```

---

# Error Contract

Todas as exceções devem seguir o mesmo formato.

### Response

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 400,
  "error": "Validation Error",
  "message": "Email é obrigatório",
  "path": "/api/v1/leads"
}
```

---

# Status de Lead

```text
NEW
ASSIGNED
CONTACTED
QUALIFIED
PROPOSAL
WON
LOST
```

---

# Status de Distribuição

```text
PENDING
ACCEPTED
REJECTED
TIMEOUT
```

---

# Roles

```text
ADMIN
MANAGER
SELLER
```

Fim do documento.
