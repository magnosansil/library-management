# Documentação da API

## Base URL

```
http://localhost:8080/api
```

## Endpoints

### Usuários

#### Criar Usuário

```http
POST /api/users
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "maxLoans": 3
}
```

#### Listar Todos os Usuários

```http
GET /api/users
```

#### Obter Usuário por ID

```http
GET /api/users/{id}
```

#### Atualizar Usuário

```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "maxLoans": 5
}
```

#### Deletar Usuário

```http
DELETE /api/users/{id}
```

---

### Livros

#### Criar Livro

```http
POST /api/books
Content-Type: application/json

{
  "title": "Dom Casmurro",
  "author": "Machado de Assis",
  "publicationDate": "1899-01-01",
  "isbn": "978-8535914093",
  "price": 29.90,
  "stockQuantity": 5,
  "availableQuantity": 5
}
```

#### Listar Todos os Livros

```http
GET /api/books
```

#### Obter Livro por ID

```http
GET /api/books/{id}
```

#### Verificar Disponibilidade de Livro

```http
GET /api/books/{id}/availability
```

**Resposta:**

```json
{
  "bookId": 1,
  "bookTitle": "Dom Casmurro",
  "totalQuantity": 5,
  "availableQuantity": 3,
  "isAvailable": true
}
```

#### Atualizar Livro

```http
PUT /api/books/{id}
Content-Type: application/json

{
  "title": "Dom Casmurro",
  "author": "Machado de Assis",
  "stockQuantity": 10
}
```

#### Deletar Livro

```http
DELETE /api/books/{id}
```

---

### Empréstimos

#### Verificar Disponibilidade de Livro (antes de emprestar)

```http
GET /api/loans/books/{bookId}/availability
```

**Resposta:** `true` ou `false`

#### Verificar se Usuário Pode Emprestar

```http
GET /api/loans/users/{userId}/can-borrow
```

**Resposta:** `true` ou `false`

#### Criar Novo Empréstimo

```http
POST /api/loans
Content-Type: application/json

{
  "userId": 1,
  "bookId": 1
}
```

**Resposta:**

```json
{
  "id": 1,
  "userId": 1,
  "userName": "João Silva",
  "bookId": 1,
  "bookTitle": "Dom Casmurro",
  "loanDate": "2024-01-15",
  "dueDate": "2024-01-29",
  "returnDate": null,
  "status": "ACTIVE"
}
```

#### Registrar Devolução

```http
PUT /api/loans/{loanId}/return
```

**Resposta:**

```json
{
  "id": 1,
  "userId": 1,
  "userName": "João Silva",
  "bookId": 1,
  "bookTitle": "Dom Casmurro",
  "loanDate": "2024-01-15",
  "dueDate": "2024-01-29",
  "returnDate": "2024-01-20",
  "status": "RETURNED"
}
```

#### Listar Empréstimos Ativos

```http
GET /api/loans/active
```

#### Listar Empréstimos Ativos de um Usuário

```http
GET /api/loans/active/user/{userId}
```

#### Verificar e Atualizar Empréstimos em Atraso

```http
GET /api/loans/check-overdue
```

Este endpoint:

- Busca empréstimos com `dueDate < hoje` e `status = ACTIVE`
- Atualiza o status para `OVERDUE`
- Adiciona à fila de notificações (usando LinkedList)

#### Obter Notificações de Empréstimos em Atraso

```http
GET /api/loans/overdue-notifications
```

Retorna a lista de empréstimos em atraso da fila de notificações (estrutura LinkedList).

#### Listar Todos os Empréstimos

```http
GET /api/loans
```

---

## Status de Empréstimo

- `ACTIVE`: Empréstimo ativo (livro ainda não devolvido)
- `RETURNED`: Livro devolvido
- `OVERDUE`: Empréstimo em atraso (data de devolução passou)

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

1. **Criar usuário:**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "João", "email": "joao@email.com"}'
```

2. **Criar livro:**

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title": "Dom Casmurro", "author": "Machado de Assis", "stockQuantity": 5, "availableQuantity": 5}'
```

3. **Verificar disponibilidade:**

```bash
curl http://localhost:8080/api/loans/books/1/availability
```

4. **Criar empréstimo:**

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "bookId": 1}'
```

5. **Verificar empréstimos ativos:**

```bash
curl http://localhost:8080/api/loans/active
```

6. **Devolver livro:**

```bash
curl -X PUT http://localhost:8080/api/loans/1/return
```

---

## Estrutura de Dados Aplicada

A estrutura de dados **LinkedList** (lista duplamente encadeada) é utilizada no serviço `NotificationService` para gerenciar a fila de notificações de empréstimos em atraso. Isso demonstra a aplicação prática das estruturas de dados estudadas no curso.
