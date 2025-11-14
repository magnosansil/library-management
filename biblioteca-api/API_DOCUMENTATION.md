# Documentação da API

## Base URL

```
http://localhost:8080/api
```

## Endpoints

### Alunos

#### Criar Aluno

```http
POST /api/students
Content-Type: application/json

{
  "matricula": "2024001",
  "nome": "João Silva",
  "cpf": "12345678901",
  "dataNascimento": "2000-05-15",
  "email": "joao.silva@exemplo.com",
  "telefone": "(11) 99999-1111"
}
```

**Campos:**

- `matricula` (obrigatório, único)
- `nome` (obrigatório)
- `cpf` (obrigatório, único, 11 dígitos)
- `dataNascimento` (obrigatório, formato: YYYY-MM-DD)
- `email` (obrigatório, único, formato válido)
- `telefone` (opcional)

#### Criar Múltiplos Alunos (Batch)

```http
POST /api/students/batch
Content-Type: application/json

[
  {
    "matricula": "2024001",
    "nome": "João Silva",
    "cpf": "12345678901",
    "dataNascimento": "2000-05-15",
    "email": "joao.silva@exemplo.com",
    "telefone": "(11) 99999-1111"
  },
  {
    "matricula": "2024002",
    "nome": "Maria Santos",
    "cpf": "98765432100",
    "dataNascimento": "2001-08-20",
    "email": "maria.santos@exemplo.com"
  }
]
```

#### Listar Todos os Alunos

```http
GET /api/students
```

#### Obter Aluno por Matrícula

```http
GET /api/students/{matricula}
```

#### Atualizar Aluno

```http
PUT /api/students/{matricula}
Content-Type: application/json

{
  "nome": "João Silva Santos",
  "cpf": "12345678901",
  "dataNascimento": "2000-05-15",
  "email": "joao.silva.santos@exemplo.com",
  "telefone": "(11) 99999-1111"
}
```

#### Deletar Aluno

```http
DELETE /api/students/{matricula}
```

---

### Livros

#### Criar Livro

```http
POST /api/books
Content-Type: application/json

{
  "isbn": "978-8535914093",
  "title": "Dom Casmurro",
  "author": "Machado de Assis",
  "coverImageUrl": "https://example.com/capa.jpg",
  "keywords": "literatura brasileira, romance, século XIX",
  "synopsis": "Romance clássico da literatura brasileira...",
  "quantity": 5
}
```

#### Criar Múltiplos Livros (Batch)

```http
POST /api/books/batch
Content-Type: application/json

[
  {
    "isbn": "978-8535914093",
    "title": "Dom Casmurro",
    "author": "Machado de Assis",
    "quantity": 5
  },
  {
    "isbn": "978-8535911234",
    "title": "O Cortiço",
    "author": "Aluísio Azevedo",
    "quantity": 3
  }
]
```

#### Listar Todos os Livros

```http
GET /api/books
```

#### Obter Livro por ISBN

```http
GET /api/books/{isbn}
```

#### Verificar Disponibilidade de Livro

```http
GET /api/books/{isbn}/availability
```

**Resposta:**

```json
{
  "bookIsbn": "978-8535914093",
  "bookTitle": "Dom Casmurro",
  "bookAuthor": "Machado de Assis",
  "quantity": 3,
  "isAvailable": true
}
```

#### Atualizar Livro

```http
PUT /api/books/{isbn}
Content-Type: application/json

{
  "title": "Dom Casmurro",
  "author": "Machado de Assis",
  "quantity": 10
}
```

#### Deletar Livro

```http
DELETE /api/books/{isbn}
```

---

### Empréstimos

#### Verificar Disponibilidade de Livro (antes de emprestar)

```http
GET /api/loans/books/{isbn}/availability
```

**Resposta:** `true` ou `false`

#### Verificar se Aluno Pode Emprestar

```http
GET /api/loans/students/{matricula}/can-borrow
```

**Resposta:** `true` ou `false`

#### Criar Novo Empréstimo

```http
POST /api/loans
Content-Type: application/json

{
  "studentMatricula": "2024001",
  "bookIsbn": "978-8535914093"
}
```

**Resposta:**

```json
{
  "id": 1,
  "studentMatricula": "2024001",
  "studentName": "João Silva",
  "bookIsbn": "978-8535914093",
  "bookTitle": "Dom Casmurro",
  "bookAuthor": "Machado de Assis",
  "loanDate": "2024-01-15T10:00:00",
  "dueDate": "2024-01-29T10:00:00",
  "returnDate": null,
  "status": "ACTIVE",
  "overdueDays": null,
  "fineAmount": null
}
```

**Nota:** `dueDate` é calculado automaticamente como `loanDate + loanPeriodDays` (configurado em `/api/settings`).

#### Registrar Devolução

```http
PUT /api/loans/{loanId}/return
```

**Resposta:**

```json
{
  "id": 1,
  "studentMatricula": "2024001",
  "studentName": "João Silva",
  "bookIsbn": "978-8535914093",
  "bookTitle": "Dom Casmurro",
  "bookAuthor": "Machado de Assis",
  "loanDate": "2024-01-15T10:00:00",
  "dueDate": "2024-01-29T10:00:00",
  "returnDate": "2024-02-05T14:30:00",
  "status": "RETURNED",
  "overdueDays": 7,
  "fineAmount": 700
}
```

**Nota:** O sistema calcula automaticamente:

- **`overdueDays`**: Diferença em dias entre `dueDate` e `returnDate` (se houver atraso)
- **`fineAmount`**: Valor da multa calculado como `overdueDays × finePerDay` (configurado em `/api/settings`)

Se não houver atraso, ambos os campos serão `0`.

#### Listar Empréstimos Ativos

```http
GET /api/loans/active
```

#### Listar Empréstimos Ativos de um Aluno

```http
GET /api/loans/active/student/{matricula}
```

#### Verificar e Atualizar Empréstimos em Atraso

```http
GET /api/loans/check-overdue
```

Este endpoint:

- Busca empréstimos com `dueDate < hoje` e `status = ACTIVE`
- Atualiza o status para `OVERDUE`

#### Listar Todos os Empréstimos

```http
GET /api/loans
```

#### Listar Empréstimos em Atraso

```http
GET /api/loans/overdue
```

#### Listar Empréstimos Devolvidos

```http
GET /api/loans/returned
```

#### Listar Empréstimos Ativos e em Atraso

```http
GET /api/loans/active-and-overdue
```

---

### Reservas

#### Criar Reserva

```http
POST /api/reservations
Content-Type: application/json

{
  "bookIsbn": "978-8535914093",
  "studentMatricula": "2024001",
  "reservationDate": "2024-01-15T10:00:00"  // Opcional
}
```

**Resposta:**

```json
{
  "id": 1,
  "bookIsbn": "978-8535914093",
  "bookTitle": "Dom Casmurro",
  "studentMatricula": "2024001",
  "studentName": "João Silva",
  "reservationDate": "2024-01-15T10:00:00",
  "queuePosition": 1,
  "status": "ACTIVE"
}
```

#### Listar Todas as Reservas

```http
GET /api/reservations
```

#### Buscar Reserva por ID

```http
GET /api/reservations/{id}
```

#### Listar Reservas de um Livro

```http
GET /api/reservations/book/{isbn}
```

**Resposta:** Lista de reservas ativas ordenadas por posição na fila (1, 2, 3...)

#### Listar Reservas de um Estudante

```http
GET /api/reservations/student/{matricula}
```

#### Cancelar Reserva

```http
DELETE /api/reservations/{id}
```

**Nota:** A fila é reorganizada automaticamente (próximas reservas avançam).

#### Efetivar Reserva

```http
PUT /api/reservations/{id}/fulfill
```

**Nota:** Marca a reserva como gerou empréstimo e reorganiza a fila automaticamente.

---

### Notificações

#### Enviar Notificação de Livro em Atraso

```http
POST /api/notifications/overdue
Content-Type: application/json

{
  "loanId": 1
}
```

**Resposta:**

```json
{
  "message": "Notificação de atraso enviada com sucesso",
  "email": "joao.silva@exemplo.com",
  "studentName": "João Silva",
  "bookTitle": "Dom Casmurro",
  "overdueDays": 6
}
```

**Validações:**

- Empréstimo deve existir
- Empréstimo não deve estar devolvido
- Estudante deve ter e-mail cadastrado
- Empréstimo deve estar em atraso (dueDate < hoje)

#### Enviar Notificação de Reserva Disponível

```http
POST /api/notifications/reservation-available
Content-Type: application/json

{
  "reservationId": 1
}
```

**Resposta:**

```json
{
  "message": "Notificação de reserva disponível enviada com sucesso",
  "email": "maria.santos@exemplo.com",
  "studentName": "Maria Santos",
  "bookTitle": "Dom Casmurro",
  "reservationId": 1
}
```

**Validações:**

- Reserva deve existir
- Reserva deve estar ativa
- Estudante deve ter e-mail cadastrado
- Livro deve estar disponível (quantity > 0)

**Nota:** As notificações são enviadas por e-mail. Configure as credenciais SMTP no arquivo `.env` (veja configuração de e-mail).

---

## Configurações Globais (`/api/settings`)

### Obter Configurações

```http
GET /api/settings
```

**Resposta:**

```json
{
  "id": 1,
  "loanPeriodDays": 14,
  "maxLoansPerStudent": 3,
  "finePerDay": 100
}
```

### Atualizar Configurações

```http
PUT /api/settings
Content-Type: application/json

{
  "loanPeriodDays": 21,
  "maxLoansPerStudent": 5,
  "finePerDay": 150
}
```

**Campos:**

- `loanPeriodDays`: Prazo padrão de devolução em dias (padrão: 14)
- `maxLoansPerStudent`: Limite máximo de empréstimos simultâneos por aluno (padrão: 3)
- `finePerDay`: Multa por dia de atraso em centavos/unidade mínima (padrão: 100)

---

## Configuração de E-mail

Para que as notificações funcionem, configure as credenciais SMTP no arquivo `.env`:

```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=senha-de-app
```

**Opções de serviço:**

- **Gmail**: Use Senha de App (https://myaccount.google.com/apppasswords)
- **Mailtrap**: Para testes (não envia e-mails reais, apenas captura)
- **SendGrid**: Para produção (100 e-mails/dia grátis)

**Nota:** As notificações são acionadas manualmente pelo front-end através das rotas de notificação.

---

## Status de Empréstimo

- `ACTIVE`: Empréstimo ativo (livro ainda não devolvido)
- `RETURNED`: Livro devolvido
- `OVERDUE`: Empréstimo em atraso (data de devolução passou)

## Sistema de Multas

O sistema calcula automaticamente multas quando um livro é devolvido com atraso:

1. **Cálculo de Dias de Atraso**: Diferença entre `dueDate` e `returnDate` (em dias)
2. **Cálculo de Multa**: `overdueDays × finePerDay` (das configurações globais)

**Exemplo:**

- `dueDate`: 2024-01-29
- `returnDate`: 2024-02-05
- `overdueDays`: 7 dias
- `finePerDay`: 100 (centavos)
- `fineAmount`: 700 (centavos) = R$ 7,00

**Nota:** A formatação para exibição em dinheiro (R$) deve ser feita no front-end.

---

## Códigos de Status HTTP

- `200 OK`: Requisição bem-sucedida
- `201 Created`: Recurso criado com sucesso
- `400 Bad Request`: Erro na requisição (dados inválidos)
- `404 Not Found`: Recurso não encontrado
- `500 Internal Server Error`: Erro interno do servidor

---

## Exemplos de Uso Completo

### Fluxo Completo de Empréstimo

1. **Criar aluno:**

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"matricula": "2024001", "nome": "João Silva", "cpf": "12345678901", "dataNascimento": "2000-05-15", "email": "joao.silva@exemplo.com", "telefone": "(11) 99999-1111"}'
```

2. **Criar livro:**

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"isbn": "978-8535914093", "title": "Dom Casmurro", "author": "Machado de Assis", "quantity": 5}'
```

3. **Verificar disponibilidade:**

```bash
curl http://localhost:8080/api/loans/books/978-8535914093/availability
```

4. **Criar empréstimo:**

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{"studentMatricula": "2024001", "bookIsbn": "978-8535914093"}'
```

5. **Verificar empréstimos ativos:**

```bash
curl http://localhost:8080/api/loans/active
```

6. **Devolver livro (com cálculo automático de multa):**

```bash
curl -X PUT http://localhost:8080/api/loans/1/return
```

**Resposta incluirá `overdueDays` e `fineAmount` se houver atraso:**

```json
{
  "id": 1,
  "studentMatricula": "2024001",
  "studentName": "João Silva",
  "bookIsbn": "978-8535914093",
  "bookTitle": "Dom Casmurro",
  "loanDate": "2024-01-15T10:00:00",
  "dueDate": "2024-01-29T10:00:00",
  "returnDate": "2024-02-05T14:30:00",
  "status": "RETURNED",
  "overdueDays": 7,
  "fineAmount": 700
}
```

---

## Estrutura de Dados Aplicada

O sistema implementa uma **Fila de Reservas** para gerenciar reservas de livros, onde cada livro pode ter até 5 reservas ordenadas. Quando uma reserva é cancelada ou efetivada, a fila é reorganizada automaticamente, demonstrando a aplicação prática de estruturas de dados.
