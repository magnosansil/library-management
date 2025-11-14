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

| Método | Rota                                         | Handler                                     | Descrição                                                                           |
| ------ | -------------------------------------------- | ------------------------------------------- | ----------------------------------------------------------------------------------- |
| GET    | `/api/loans`                                 | `LoanController.getAllLoans()`              | Listar todos os empréstimos                                                         |
| GET    | `/api/loans/active`                          | `LoanController.getActiveLoans()`           | Listar empréstimos ativos                                                           |
| GET    | `/api/loans/overdue`                         | `LoanController.getOverdueLoans()`          | Listar empréstimos em atraso (status OVERDUE)                                       |
| GET    | `/api/loans/returned`                        | `LoanController.getReturnedLoans()`         | Listar empréstimos devolvidos (status RETURNED)                                     |
| GET    | `/api/loans/active-and-overdue`              | `LoanController.getActiveAndOverdueLoans()` | Listar empréstimos ativos e em atraso juntos                                        |
| GET    | `/api/loans/active/student/{matricula}`      | `LoanController.getActiveLoansByStudent()`  | Empréstimos ativos de um aluno                                                      |
| GET    | `/api/loans/books/{isbn}/availability`       | `LoanController.checkBookAvailability()`    | Verificar disponibilidade antes de emprestar                                        |
| GET    | `/api/loans/students/{matricula}/can-borrow` | `LoanController.canStudentBorrow()`         | Verificar se aluno pode emprestar                                                   |
| GET    | `/api/loans/check-overdue`                   | `LoanController.checkOverdueLoans()`        | Verificar e atualizar empréstimos em atraso                                         |
| POST   | `/api/loans`                                 | `LoanController.createLoan()`               | Criar novo empréstimo                                                               |
| PUT    | `/api/loans/{loanId}/return`                 | `LoanController.returnLoan()`               | Registrar devolução de livro (calcula multa automaticamente, data opcional no body) |

**Controller:** `com.biblioteca.controller.LoanController`

---

## 📋 Reservas (`/api/reservations`)

| Método | Rota                                    | Handler                                            | Descrição                                                        |
| ------ | --------------------------------------- | -------------------------------------------------- | ---------------------------------------------------------------- |
| GET    | `/api/reservations`                     | `ReservationController.getAllReservations()`       | Listar todas as reservas                                         |
| GET    | `/api/reservations/{id}`                | `ReservationController.getReservationById()`       | Buscar reserva por ID                                            |
| GET    | `/api/reservations/book/{isbn}`         | `ReservationController.getReservationsByBook()`    | Listar reservas ativas de um livro (ordem da fila)               |
| GET    | `/api/reservations/student/{matricula}` | `ReservationController.getReservationsByStudent()` | Listar reservas ativas de um estudante                           |
| POST   | `/api/reservations`                     | `ReservationController.createReservation()`        | Criar nova reserva (máximo 5 por livro, fila ordenada)           |
| DELETE | `/api/reservations/{id}`                | `ReservationController.cancelReservation()`        | Cancelar reserva (reorganiza fila automaticamente)               |
| PUT    | `/api/reservations/{id}/fulfill`        | `ReservationController.fulfillReservation()`       | Efetivar reserva (marcar como gerou empréstimo, reorganiza fila) |

**Controller:** `com.biblioteca.controller.ReservationController`

---

## 📧 Notificações (`/api/notifications`)

| Método | Rota                                       | Handler                                                         | Descrição                                           |
| ------ | ------------------------------------------ | --------------------------------------------------------------- | --------------------------------------------------- |
| POST   | `/api/notifications/overdue`               | `NotificationController.sendOverdueNotification()`              | Enviar notificação de livro em atraso por e-mail    |
| POST   | `/api/notifications/reservation-available` | `NotificationController.sendReservationAvailableNotification()` | Enviar notificação de reserva disponível por e-mail |

**Controller:** `com.biblioteca.controller.NotificationController`

**Nota:** Requer configuração de e-mail no arquivo `.env` (MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD).

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

- **Fila de Reservas** - Sistema de reservas implementa uma fila ordenada com máximo de 5 posições por livro

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
GET    /api/loans/overdue                      → getOverdueLoans()
GET    /api/loans/returned                     → getReturnedLoans()
GET    /api/loans/active-and-overdue            → getActiveAndOverdueLoans()
GET    /api/loans/active/student/{matricula}   → getActiveLoansByStudent(String matricula)
GET    /api/loans/books/{isbn}/availability    → checkBookAvailability(String isbn)
GET    /api/loans/students/{matricula}/can-borrow → canStudentBorrow(String matricula)
GET    /api/loans/check-overdue                 → checkOverdueLoans()
POST   /api/loans                               → createLoan(@RequestBody LoanRequestDTO request)
PUT    /api/loans/{loanId}/return               → returnLoan(Long loanId, LoanReturnDTO returnDTO) // Calcula multa automaticamente, data opcional
```

### Reservas

```java
// Controller: ReservationController
GET    /api/reservations                        → getAllReservations()
GET    /api/reservations/{id}                   → getReservationById(Long id)
GET    /api/reservations/book/{isbn}             → getReservationsByBook(String isbn)
GET    /api/reservations/student/{matricula}     → getReservationsByStudent(String matricula)
POST   /api/reservations                         → createReservation(@RequestBody ReservationRequestDTO request)
DELETE /api/reservations/{id}                    → cancelReservation(Long id) // Reorganiza fila
PUT    /api/reservations/{id}/fulfill            → fulfillReservation(Long id) // Marca como efetivada, reorganiza fila
```

### Notificações

```java
// Controller: NotificationController
POST   /api/notifications/overdue                 → sendOverdueNotification(@RequestBody OverdueNotificationDTO request)
POST   /api/notifications/reservation-available    → sendReservationAvailableNotification(@RequestBody ReservationAvailableNotificationDTO request)
```

### Alunos

```java
// Controller: StudentController
GET    /api/students                    → getAllStudents()
GET    /api/students/{matricula}        → getStudentByMatricula(String matricula)
POST   /api/students                    → createStudent(@RequestBody Student student) // Agora inclui email (obrigatório) e telefone (opcional)
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

**Total de Rotas:** 29

- **Livros:** 7 rotas
- **Empréstimos:** 11 rotas
- **Reservas:** 7 rotas
- **Notificações:** 2 rotas
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

---

## 📧 Sistema de Notificações por E-mail

O sistema permite enviar notificações por e-mail para estudantes sobre:

- Livros em atraso
- Reservas disponíveis

**Configuração necessária:**

- Configure credenciais SMTP no arquivo `.env`
- Estudantes devem ter e-mail cadastrado
- Notificações são acionadas manualmente pelo front-end

**Serviços recomendados:**

- **Gmail**: Para testes (use Senha de App)
- **Mailtrap**: Para desenvolvimento (captura e-mails sem enviar)
- **SendGrid**: Para produção (100 e-mails/dia grátis)

---

**Última atualização:** 14-11-2025
