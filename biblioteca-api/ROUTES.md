# 🗺️ Rotas da API - Arquivo Centralizado

Este arquivo centraliza todas as rotas da API para fácil referência e manutenção.

## 📚 Livros (`/api/books`)

| Método | Rota                             | Handler                                            | Descrição                      |
| ------ | -------------------------------- | -------------------------------------------------- | ------------------------------ |
| GET    | `/api/books`                     | `BookManagementController.getAllBooks()`           | Listar todos os livros         |
| GET    | `/api/books/{isbn}`              | `BookManagementController.getBookByIsbn()`         | Buscar livro por ISBN          |
| GET    | `/api/books/{isbn}/availability` | `BookManagementController.checkBookAvailability()` | Verificar disponibilidade      |
| POST   | `/api/books`                     | `BookManagementController.createBook()`            | Criar novo livro (um por vez)  |
| POST   | `/api/books/batch`               | `BookManagementController.createBooksBatch()`      | Criar múltiplos livros (array) |
| PUT    | `/api/books/{isbn}`              | `BookManagementController.updateBook()`            | Atualizar livro                |
| DELETE | `/api/books/{isbn}`              | `BookManagementController.deleteBook()`            | Excluir livro por ISBN         |

**Controller:** `com.biblioteca.controller.BookManagementController`

---

## 📖 Empréstimos (`/api/loans`)

| Método | Rota                                   | Handler                                    | Descrição                                    |
| ------ | -------------------------------------- | ------------------------------------------ | -------------------------------------------- |
| GET    | `/api/loans`                           | `LoanController.getAllLoans()`             | Listar todos os empréstimos                  |
| GET    | `/api/loans/active`                    | `LoanController.getActiveLoans()`          | Listar empréstimos ativos                    |
| GET    | `/api/loans/active/user/{userId}`      | `LoanController.getActiveLoansByUser()`    | Empréstimos ativos de um usuário             |
| GET    | `/api/loans/books/{isbn}/availability` | `LoanController.checkBookAvailability()`   | Verificar disponibilidade antes de emprestar |
| GET    | `/api/loans/users/{userId}/can-borrow` | `LoanController.canUserBorrow()`           | Verificar se usuário pode emprestar          |
| GET    | `/api/loans/check-overdue`             | `LoanController.checkOverdueLoans()`       | Verificar e atualizar empréstimos em atraso  |
| GET    | `/api/loans/overdue-notifications`     | `LoanController.getOverdueNotifications()` | Notificações de empréstimos em atraso        |
| POST   | `/api/loans`                           | `LoanController.createLoan()`              | Criar novo empréstimo                        |
| PUT    | `/api/loans/{loanId}/return`           | `LoanController.returnLoan()`              | Registrar devolução de livro                 |

**Controller:** `com.biblioteca.controller.LoanController`

---

## 👥 Usuários (`/api/users`)

| Método | Rota              | Handler                        | Descrição                |
| ------ | ----------------- | ------------------------------ | ------------------------ |
| GET    | `/api/users`      | `UserController.getAllUsers()` | Listar todos os usuários |
| GET    | `/api/users/{id}` | `UserController.getUserById()` | Buscar usuário por ID    |
| POST   | `/api/users`      | `UserController.createUser()`  | Criar novo usuário       |
| PUT    | `/api/users/{id}` | `UserController.updateUser()`  | Atualizar usuário        |
| DELETE | `/api/users/{id}` | `UserController.deleteUser()`  | Excluir usuário          |

**Controller:** `com.biblioteca.controller.UserController`

---

## 🏥 Sistema (`/` e `/api/health`)

| Método | Rota          | Handler                     | Descrição                               |
| ------ | ------------- | --------------------------- | --------------------------------------- |
| GET    | `/`           | `IndexController.index()`   | Página inicial com informações da API   |
| GET    | `/api/health` | `HealthController.health()` | Health check e status do banco de dados |

**Controllers:**

- `com.biblioteca.controller.IndexController`
- `com.biblioteca.controller.HealthController`

---

## 📝 Estrutura de Dados Aplicada

- **LinkedList** (lista duplamente encadeada) - Usada em `NotificationService` para gerenciar fila de notificações de empréstimos em atraso

---

## 🔗 Mapeamento de Rotas para Handlers

### Livros

```java
// Controller: BookManagementController
GET    /api/books                    → getAllBooks()
GET    /api/books/{isbn}             → getBookByIsbn(String isbn)
GET    /api/books/{isbn}/availability → checkBookAvailability(String isbn)
POST   /api/books                    → createBook(@RequestBody Book book)
POST   /api/books/batch              → createBooksBatch(@RequestBody List<Book> books)
PUT    /api/books/{isbn}             → updateBook(String isbn, @RequestBody Book book)
DELETE /api/books/{isbn}             → deleteBook(String isbn)
```

### Empréstimos

```java
// Controller: LoanController
GET    /api/loans                              → getAllLoans()
GET    /api/loans/active                       → getActiveLoans()
GET    /api/loans/active/user/{userId}          → getActiveLoansByUser(Long userId)
GET    /api/loans/books/{isbn}/availability    → checkBookAvailability(String isbn)
GET    /api/loans/users/{userId}/can-borrow     → canUserBorrow(Long userId)
GET    /api/loans/check-overdue                 → checkOverdueLoans()
GET    /api/loans/overdue-notifications         → getOverdueNotifications()
POST   /api/loans                               → createLoan(@RequestBody LoanRequestDTO request)
PUT    /api/loans/{loanId}/return               → returnLoan(Long loanId)
```

### Usuários

```java
// Controller: UserController
GET    /api/users      → getAllUsers()
GET    /api/users/{id} → getUserById(Long id)
POST   /api/users      → createUser(@RequestBody User user)
PUT    /api/users/{id} → updateUser(Long id, @RequestBody User user)
DELETE /api/users/{id} → deleteUser(Long id)
```

### Sistema

```java
// Controller: IndexController
GET / → index()

// Controller: HealthController
GET /api/health → health()
```

---

## 📋 Resumo Rápido

**Total de Rotas:** 21

- **Livros:** 7 rotas
- **Empréstimos:** 9 rotas
- **Usuários:** 5 rotas
- **Sistema:** 2 rotas

---

## 🔄 Para Adicionar Nova Rota

1. Adicione o método no controller apropriado
2. Documente aqui nesta tabela
3. Atualize o `IndexController` se necessário

---

**Última atualização:** 2024-11-14
