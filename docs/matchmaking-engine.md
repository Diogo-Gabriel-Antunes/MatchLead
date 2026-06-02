# matchmaking-engine.md

# Matchmaking Engine

Versão: 1.0

---

# Objetivo

A Matchmaking Engine é o núcleo do sistema responsável por analisar leads, calcular score, identificar os vendedores mais adequados e gerenciar o processo de distribuição até que o lead seja efetivamente aceito.

A engine deve ser:

* Determinística
* Auditável
* Escalável
* Explicável

Toda decisão tomada pela engine deve poder ser rastreada.

---

# Fluxo Geral

```text
Lead Capturado
        ↓
Enriquecimento
        ↓
Cálculo de Score
        ↓
Ranking de Vendedores
        ↓
Notificação
        ↓
Aceite?
   ├─ Sim → Alocação
   └─ Não → Próximo da Fila
```

---

# Módulo 1 - Captura de Lead

## Entrada

Lead recebido via:

* API
* CRM
* Landing Page
* Webhook

## Dados mínimos

```text
nome
telefone ou email
origem
```

## Evento Gerado

```text
LeadCaptured
```

---

# Módulo 2 - Enriquecimento

## Objetivo

Complementar informações antes do cálculo de score.

## Dados Enriquecidos

### Perfil

```text
idade
cidade
estado
região
```

### Fonte

```text
utm_source
utm_medium
utm_campaign
```

### Dispositivo

```text
desktop
mobile
tablet
```

### Histórico

```text
interações anteriores
leads anteriores
compras anteriores
```

## Evento

```text
LeadEnriched
```

---

# Módulo 3 - Scoring

## Objetivo

Determinar o potencial comercial do lead.

## Escala

```text
0 a 100
```

---

## Composição

### Perfil Demográfico

Peso:

```text
30%
```

Critérios:

* Região
* Faixa etária
* Faixa de renda

Resultado:

```text
0 a 30
```

---

### Intenção de Compra

Peso:

```text
50%
```

Critérios:

* Páginas visitadas
* Cliques
* Tempo de navegação
* Formulários preenchidos

Resultado:

```text
0 a 50
```

---

### Histórico

Peso:

```text
20%
```

Critérios:

* Contatos anteriores
* Conversões anteriores
* Relacionamento prévio

Resultado:

```text
0 a 20
```

---

## Fórmula

```text
totalScore =
demographicScore +
intentScore +
historyScore
```

Resultado:

```text
0 a 100
```

---

# Módulo 4 - Ranking de Vendedores

## Objetivo

Gerar lista ordenada de candidatos.

---

## Critérios Eliminatórios

O vendedor será removido da lista caso:

* esteja inativo
* esteja bloqueado
* não atenda a região
* tenha atingido capacidade diária

---

## Critérios de Ordenação

### Compatibilidade Regional

Peso:

```text
40%
```

---

### Especialidade

Peso:

```text
30%
```

---

### Capacidade Atual

Peso:

```text
20%
```

Menor carga recebe maior pontuação.

---

### Performance Histórica

Peso:

```text
10%
```

Baseado em:

* taxa de conversão
* tempo médio de resposta

---

## Resultado

Lista ordenada:

```text
1. Seller A
2. Seller B
3. Seller C
4. Seller D
```

---

# Módulo 5 - Explicabilidade

Toda decisão deve possuir justificativa.

Exemplo:

```text
Lead score: 87

Seller escolhido:
João Silva

Motivos:

- Região compatível
- Especialidade compatível
- Menor carga atual
- Conversão acima da média
```

---

# Módulo 6 - Fila de Distribuição

## Objetivo

Controlar tentativas de atribuição.

Exemplo:

```text
Fila

1. João
2. Maria
3. Carlos
4. Ana
```

---

# Módulo 7 - Notificação

## Objetivo

Solicitar aceite.

## Conteúdo

```text
Nome do Lead
Score
Origem
Motivo da seleção
```

---

## Canais

* Email
* Push
* Webhook

---

## Evento

```text
LeadOffered
```

---

# Módulo 8 - Aceite

## Fluxo

```text
Lead enviado
↓
Vendedor aceita
↓
Lead atribuído
↓
CRM atualizado
```

---

## Eventos

```text
LeadAccepted
LeadAssigned
```

---

# Módulo 9 - Recusa

## Fluxo

```text
Lead enviado
↓
Vendedor recusa
↓
Próximo da fila
```

---

## Eventos

```text
LeadRejected
```

---

# Módulo 10 - Timeout

## Objetivo

Evitar leads parados.

---

## Tempo Padrão

```text
120 segundos
```

Configurável.

---

## Fluxo

```text
Lead enviado
↓
Sem resposta
↓
Timeout
↓
Próximo vendedor
```

---

## Evento

```text
LeadTimeout
```

---

# Módulo 11 - Alocação Final

## Objetivo

Concluir distribuição.

Ações:

* vincular lead ao vendedor
* atualizar CRM
* registrar histórico
* atualizar métricas

---

## Evento

```text
LeadAssigned
```

---

# Persistência

## lead_scores

```text
id
lead_id

demographic_score
intent_score
history_score

total_score

created_at
```

---

## seller_rankings

```text
id
lead_id
seller_id

ranking_position

ranking_score

created_at
```

---

## seller_queue

```text
id
lead_id
seller_id

queue_position

status

PENDING
ACCEPTED
REJECTED
TIMEOUT

created_at
```

---

## match_explanations

```text
id
lead_id
seller_id

reason

created_at
```

---

# Redis

Utilizar para:

* ranking temporário
* fila de distribuição
* cache de vendedores
* cache de score

TTL padrão:

```text
10 minutos
```

---

# Eventos Assíncronos

## Entrada

```text
LeadCaptured
```

---

## Processamento

```text
LeadEnriched
LeadScored
LeadRanked
LeadOffered
```

---

## Saída

```text
LeadAccepted
LeadRejected
LeadTimeout
LeadAssigned
```

---

# Métricas

Monitorar:

* Leads recebidos
* Leads distribuídos
* Leads rejeitados
* Leads expirados
* Tempo médio de aceite
* Conversão por vendedor
* Conversão por região
* Conversão por origem

---

# Critérios de Aceitação

A engine será considerada concluída quando:

* Score for calculado
* Ranking for gerado
* Fila for criada
* Notificação for enviada
* Aceite funcionar
* Recusa funcionar
* Timeout funcionar
* Fallback funcionar
* Histórico for registrado
* Métricas forem atualizadas

Fim do documento.
