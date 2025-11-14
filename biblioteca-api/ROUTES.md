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

| Método | Rota                                         | Handler                                    | Descrição                                                    |
| ------ | -------------------------------------------- | ------------------------------------------ | ------------------------------------------------------------ |
| GET    | `/api/loans`                                 | `LoanController.getAllLoans()`             | Listar todos os empréstimos                                  |
| GET    | `/api/loans/active`                          | `LoanController.getActiveLoans()`          | Listar empréstimos ativos                                    |
| GET    | `/api/loans/active/student/{matricula}`      | `LoanController.getActiveLoansByStudent()` | Empréstimos ativos de um aluno                               |
| GET    | `/api/loans/books/{isbn}/availability`       | `LoanController.checkBookAvailability()`   | Verificar disponibilidade antes de emprestar                 |
| GET    | `/api/loans/students/{matricula}/can-borrow` | `LoanController.canStudentBorrow()`        | Verificar se aluno pode emprestar                            |
| GET    | `/api/loans/check-overdue`                   | `LoanController.checkOverdueLoans()`       | Verificar e atualizar empréstimos em atraso                  |
| GET    | `/api/loans/overdue-notifications`           | `LoanController.getOverdueNotifications()` | Notificações de empréstimos em atraso                        |
| POST   | `/api/loans`                                 | `LoanController.createLoan()`              | Criar novo empréstimo                                        |
| PUT    | `/api/loans/{loanId}/return`                 | `LoanController.returnLoan()`              | Registrar devolução de livro (calcula multa automaticamente) |

**Controller:** `com.biblioteca.controller.LoanController`

---

## 👥 Alunos (`/api/students`)

| Método | Rota                        | Handler                                     | Descrição                      |
| ------ | --------------------------- | ------------------------------------------- | ------------------------------ |
| GET    | `/api/students`             | `StudentController.getAllStudents()`        | Listar todos os alunos         |
| GET    | `/api/students/{matricula}` | `StudentController.getStudentByMatricula()` | Buscar aluno por matrícula     |
| POST   | `/api/students`             | `StudentController.createStudent()`         | Criar novo aluno (um por vez)  |
| POST   | `/api/students/batch`       | `StudentController.createStudentsBatch()`   | Criar múltiplos alunos (array) |
| PUT    | `/api/students/{matricula}` | `StudentController.updateStudent()`         | Atualizar aluno                |
| DELETE | `/api/students/{matricula}` | `StudentController.deleteStudent()`         | Excluir aluno por matrícula    |

**Controller:** `com.biblioteca.controller.StudentController`

---

## ⚙️ Configurações Globais (`/api/settings`)

| Método | Rota            | Handler                                      | Descrição                       |
| ------ | --------------- | -------------------------------------------- | ------------------------------- |
| GET    | `/api/settings` | `LibrarySettingsController.getSettings()`    | Obter configurações globais     |
| PUT    | `/api/settings` | `LibrarySettingsController.updateSettings()` | Atualizar configurações globais |

**Controller:** `com.biblioteca.controller.LibrarySettingsController`

**Campos configuráveis:**

- `loanPeriodDays`: Prazo padrão de devolução em dias (padrão: 14)
- `maxLoansPerStudent`: Limite máximo de empréstimos simultâneos por aluno (padrão: 3)
- `finePerDay`: Multa por dia de atraso em centavos/unidade mínima (padrão: 100)

---

## 🏥 Sistema (`/` e `/api/health`)

| Método | Rota          | Handler                           | Descrição                               |
| ------ | ------------- | --------------------------------- | --------------------------------------- |
| GET    | `/`           | `IndexController.index()`         | Página inicial com informações da API   |
| GET    | `/api/health` | `HealthController.health()`       | Health check e status do banco de dados |
| GET    | `/api/routes` | `RoutesController.getAllRoutes()` | Listar todas as rotas (este arquivo)    |

**Controllers:**

- `com.biblioteca.controller.IndexController`
- `com.biblioteca.controller.HealthController`
- `com.biblioteca.controller.RoutesController`

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
GET    /api/loans/active/student/{matricula}   → getActiveLoansByStudent(String matricula)
GET    /api/loans/books/{isbn}/availability    → checkBookAvailability(String isbn)
GET    /api/loans/students/{matricula}/can-borrow → canStudentBorrow(String matricula)
GET    /api/loans/check-overdue                 → checkOverdueLoans()
GET    /api/loans/overdue-notifications         → getOverdueNotifications()
POST   /api/loans                               → createLoan(@RequestBody LoanRequestDTO request)
PUT    /api/loans/{loanId}/return               → returnLoan(Long loanId) // Calcula multa automaticamente
```

### Alunos

```java
// Controller: StudentController
GET    /api/students                    → getAllStudents()
GET    /api/students/{matricula}        → getStudentByMatricula(String matricula)
POST   /api/students                    → createStudent(@RequestBody Student student)
POST   /api/students/batch              → createStudentsBatch(@RequestBody List<Student> students)
PUT    /api/students/{matricula}        → updateStudent(String matricula, @RequestBody Student student)
DELETE /api/students/{matricula}       → deleteStudent(String matricula)
```

### Configurações

```java
// Controller: LibrarySettingsController
GET    /api/settings  → getSettings()
PUT    /api/settings  → updateSettings(@RequestBody LibrarySettings settings)
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

**Total de Rotas:** 25

- **Livros:** 7 rotas
- **Empréstimos:** 9 rotas
- **Alunos:** 6 rotas
- **Configurações:** 2 rotas
- **Sistema:** 3 rotas

## 💰 Sistema de Multas

Ao devolver um livro (`PUT /api/loans/{loanId}/return`), o sistema calcula automaticamente:

- **`overdueDays`**: Diferença em dias entre `dueDate` e `returnDate` (se houver atraso)
- **`fineAmount`**: Valor da multa = `overdueDays × finePerDay` (das configurações globais)

**Exemplo:**

- Devolução com 7 dias de atraso
- `finePerDay` = 100 (centavos)
- `fineAmount` = 700 (centavos) = R$ 7,00

A formatação para exibição em dinheiro deve ser feita no front-end.

---

## 🔄 Para Adicionar Nova Rota

1. Adicione o método no controller apropriado
2. Documente aqui nesta tabela
3. Atualize o `IndexController` se necessário

---

**Última atualização:** 2024-11-14
