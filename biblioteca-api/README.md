# Biblioteca API

API REST para gerenciamento de biblioteca desenvolvida em Java com Spring Boot. Esta aplicação implementa funcionalidades de controle de empréstimos utilizando estruturas de dados customizadas.

## 📋 Funcionalidades

### Controle de Empréstimos

- ✅ Verificar disponibilidade de livro antes de emprestar (READ)
- ✅ Atualizar estoque automaticamente
- ✅ Limitar número máximo de empréstimo simultâneo por aluno
- ✅ Gerar relatório de empréstimos por status (ACTIVE, OVERDUE, RETURNED)
- ✅ Registrar devolução de livro com cálculo automático de multa
- ✅ Registrar novo empréstimo de livro
- ✅ Atualização automática de status baseada em datas
- ✅ Sistema de multas configurável (multa por dia de atraso)
- ✅ Sistema de reservas com fila ordenada (máximo 5 por livro)
- ✅ Sistema de notificações por e-mail (livros em atraso e reservas disponíveis)

## 🏗️ Estruturas de Dados Aplicadas

Este projeto implementa uma **Fila de Reservas** para gerenciar reservas de livros, onde cada livro pode ter até 5 reservas ordenadas. Quando uma reserva é cancelada ou efetivada, a fila é reorganizada automaticamente.

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Lombok**

## 📦 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Configuração e Instalação

### 1. Configurar PostgreSQL

#### Opção A: Banco na Nuvem (Recomendado para trabalho em equipe) ⭐

**Opções gratuitas recomendadas:**

- **Neon** (Mais recomendado): https://neon.tech - Serverless PostgreSQL, 0.5 GB grátis, setup em 2 minutos
- **Supabase**: https://supabase.com - 500 MB grátis, interface completa
- **Railway**: https://railway.app - $5 crédito grátis/mês, deploy fácil
- **Render**: https://render.com - 90 dias grátis

**Setup rápido com Neon:**

1. Acesse https://neon.tech e crie uma conta (pode usar GitHub)
2. Clique em **"New Project"** → Nome: `biblioteca-db`
3. Copie a connection string do dashboard
4. Configure variáveis de ambiente (veja passo 2 abaixo)

#### Opção B: Usando Docker (Local)

```bash
# Criar e iniciar container PostgreSQL
docker run --name biblioteca-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=biblioteca_db \
  -p 5432:5432 \
  -d postgres:15
```

#### Opção C: Instalação Local

1. Instale o PostgreSQL em sua máquina
2. Crie um banco de dados:

```sql
CREATE DATABASE biblioteca_db;
```

### 2. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto (baseado em `.env.example`):

```bash
# .env
DATABASE_URL=jdbc:postgresql://seu-host:5432/seu-db?sslmode=require
DB_USERNAME=usuario
DB_PASSWORD=senha

# Configurações de E-mail (opcional - necessário para notificações)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=senha-de-app
```

**⚠️ IMPORTANTE**: O arquivo `.env` está no `.gitignore` e NÃO deve ser commitado!

**📧 Nota sobre E-mail:** Para usar notificações por e-mail, configure as credenciais SMTP. Para Gmail, use uma Senha de App (não a senha normal). Veja mais detalhes na seção de notificações.

### 3. Configurar Prisma ORM (Opcional - para gerenciamento de schema)

**Nota:** Prisma é tradicionalmente usado com Node.js/TypeScript. Para Java, utilizamos JPA/Hibernate. No entanto, você pode usar Prisma para gerenciar o schema do banco de forma independente.

#### Instalação do Prisma CLI

```bash
# Usando npm (requer Node.js instalado)
npm install -g prisma

# Ou usando yarn
yarn global add prisma
```

#### Configurar Prisma no Projeto

1. Crie um arquivo `prisma/schema.prisma` na raiz do projeto:

```prisma
generator client {
  provider = "prisma-client-js"
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}
```

2. Configure a variável de ambiente `DATABASE_URL`:

```bash
# Windows (PowerShell)
$env:DATABASE_URL="postgresql://postgres:postgres@localhost:5432/biblioteca_db?schema=public"

# Linux/Mac
export DATABASE_URL="postgresql://postgres:postgres@localhost:5432/biblioteca_db?schema=public"
```

3. Crie o schema inicial:

```prisma
// prisma/schema.prisma
model Book {
  id                BigInt    @id @default(autoincrement())
  title             String
  author            String?
  publicationDate   DateTime? @map("publication_date")
  isbn              String?
  price             Float?
  stockQuantity     Int       @default(0) @map("stock_quantity")
  availableQuantity Int       @default(0) @map("available_quantity")
  loans             Loan[]

  @@map("books")
}

model User {
  id        BigInt    @id @default(autoincrement())
  name      String
  email     String    @unique
  maxLoans  Int       @default(3) @map("max_loans")
  createdAt DateTime  @default(now()) @map("created_at")
  loans     Loan[]

  @@map("users")
}

model Loan {
  id         BigInt    @id @default(autoincrement())
  userId     BigInt    @map("user_id")
  bookId     BigInt    @map("book_id")
  loanDate   DateTime  @map("loan_date")
  dueDate    DateTime  @map("due_date")
  returnDate DateTime? @map("return_date")
  status     String
  createdAt  DateTime  @default(now()) @map("created_at")

  user       User      @relation(fields: [userId], references: [id])
  book       Book      @relation(fields: [bookId], references: [id])

  @@map("loans")
}
```

4. Execute as migrações do Prisma:

```bash
# Gerar o Prisma Client
npx prisma generate

# Criar migração inicial
npx prisma migrate dev --name init

# Aplicar migrações ao banco
npx prisma migrate deploy
```

**Importante:** O Spring Boot usa JPA/Hibernate para gerenciar o schema automaticamente. O Prisma pode ser usado como ferramenta complementar para visualização e gerenciamento do banco, mas não é necessário para o funcionamento da API.

### 4. Compilar e Executar

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 📚 Endpoints da API

### Empréstimos

| Método | Endpoint                                     | Descrição                                           |
| ------ | -------------------------------------------- | --------------------------------------------------- |
| GET    | `/api/loans`                                 | Listar todos os empréstimos                         |
| GET    | `/api/loans/active`                          | Listar empréstimos ativos                           |
| GET    | `/api/loans/overdue`                         | Listar empréstimos em atraso (OVERDUE)              |
| GET    | `/api/loans/returned`                        | Listar empréstimos devolvidos (RETURNED)            |
| GET    | `/api/loans/active-and-overdue`              | Listar empréstimos ativos e em atraso               |
| GET    | `/api/loans/active/student/{matricula}`      | Empréstimos ativos de um aluno                      |
| GET    | `/api/loans/books/{isbn}/availability`       | Verificar disponibilidade de livro                  |
| GET    | `/api/loans/students/{matricula}/can-borrow` | Verificar se aluno pode emprestar                   |
| GET    | `/api/loans/check-overdue`                   | Verificar e atualizar empréstimos em atraso         |
| POST   | `/api/loans`                                 | Criar novo empréstimo                               |
| PUT    | `/api/loans/{loanId}/return`                 | Registrar devolução (calcula multa automaticamente) |

### Reservas

| Método | Endpoint                                | Descrição                                          |
| ------ | --------------------------------------- | -------------------------------------------------- |
| GET    | `/api/reservations`                     | Listar todas as reservas                           |
| GET    | `/api/reservations/{id}`                | Buscar reserva por ID                              |
| GET    | `/api/reservations/book/{isbn}`         | Listar reservas ativas de um livro (ordem da fila) |
| GET    | `/api/reservations/student/{matricula}` | Listar reservas ativas de um estudante             |
| POST   | `/api/reservations`                     | Criar nova reserva (máximo 5 por livro)            |
| DELETE | `/api/reservations/{id}`                | Cancelar reserva (reorganiza fila)                 |
| PUT    | `/api/reservations/{id}/fulfill`        | Efetivar reserva (marcar como gerou empréstimo)    |

### Notificações

| Método | Endpoint                                   | Descrição                                           |
| ------ | ------------------------------------------ | --------------------------------------------------- |
| POST   | `/api/notifications/overdue`               | Enviar notificação de livro em atraso por e-mail    |
| POST   | `/api/notifications/reservation-available` | Enviar notificação de reserva disponível por e-mail |

### Livros

| Método | Endpoint                           | Descrição                           |
| ------ | ---------------------------------- | ----------------------------------- |
| GET    | `/api/books/{bookId}/availability` | Verificar disponibilidade detalhada |

## 📝 Exemplos de Uso

### Criar um Empréstimo

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "studentMatricula": "2024001",
    "bookIsbn": "978-8535914093"
  }'
```

### Verificar Disponibilidade

```bash
curl http://localhost:8080/api/loans/books/978-8535914093/availability
```

### Registrar Devolução

```bash
# Devolução com data atual (automática)
curl -X PUT http://localhost:8080/api/loans/1/return

# Devolução com data específica (opcional)
curl -X PUT http://localhost:8080/api/loans/1/return \
  -H "Content-Type: application/json" \
  -d '{"returnDate": "2024-01-20T14:30:00"}'
```

### Obter Empréstimos por Status

```bash
# Empréstimos ativos
curl http://localhost:8080/api/loans/active

# Empréstimos em atraso
curl http://localhost:8080/api/loans/overdue

# Empréstimos devolvidos
curl http://localhost:8080/api/loans/returned

# Empréstimos ativos e em atraso juntos
curl http://localhost:8080/api/loans/active-and-overdue
```

### Verificar Empréstimos em Atraso

```bash
curl http://localhost:8080/api/loans/check-overdue
```

### Criar uma Reserva

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "bookIsbn": "978-8535914093",
    "studentMatricula": "2024001"
  }'
```

### Listar Reservas de um Livro

```bash
curl http://localhost:8080/api/reservations/book/978-8535914093
```

### Cancelar Reserva

```bash
curl -X DELETE http://localhost:8080/api/reservations/1
```

### Efetivar Reserva

```bash
curl -X PUT http://localhost:8080/api/reservations/1/fulfill
```

### Enviar Notificação de Livro em Atraso

```bash
curl -X POST http://localhost:8080/api/notifications/overdue \
  -H "Content-Type: application/json" \
  -d '{"loanId": 1}'
```

### Enviar Notificação de Reserva Disponível

```bash
curl -X POST http://localhost:8080/api/notifications/reservation-available \
  -H "Content-Type: application/json" \
  -d '{"reservationId": 1}'
```

## 🗄️ Estrutura do Banco de Dados

### Tabela: `books`

- `isbn` (VARCHAR, PK, UNIQUE, NOT NULL)
- `title` (VARCHAR, NOT NULL)
- `author` (VARCHAR, NOT NULL)
- `cover_image_url` (VARCHAR)
- `keywords` (VARCHAR)
- `synopsis` (TEXT)
- `entry_date` (DATE, NOT NULL)
- `quantity` (INTEGER, NOT NULL, padrão: 0)
- `active_reservations_count` (INTEGER, NOT NULL, padrão: 0) - Contador de reservas ativas

### Tabela: `students`

- `matricula` (VARCHAR, PK)
- `nome` (VARCHAR, NOT NULL)
- `cpf` (VARCHAR, UNIQUE, NOT NULL)
- `data_nascimento` (DATE, NOT NULL)
- `email` (VARCHAR, UNIQUE, NOT NULL) - E-mail para notificações
- `telefone` (VARCHAR, opcional) - Telefone de contato
- `reservations_count` (INTEGER, NOT NULL, padrão: 0) - Total de reservas registradas

### Tabela: `loans`

- `id` (BIGINT, PK)
- `student_matricula` (VARCHAR, FK -> students.matricula)
- `book_isbn` (VARCHAR, FK -> books.isbn)
- `loan_date` (TIMESTAMP, NOT NULL)
- `due_date` (TIMESTAMP, NOT NULL)
- `return_date` (TIMESTAMP)
- `status` (VARCHAR, NOT NULL) - ACTIVE, RETURNED, OVERDUE (atualizado automaticamente)
- `overdue_days` (INTEGER) - Dias de atraso (calculado na devolução)
- `fine_amount` (INTEGER) - Valor da multa em centavos (calculado na devolução)
- `created_at` (TIMESTAMP, NOT NULL)

### Tabela: `reservations`

- `id` (BIGSERIAL, PK)
- `book_isbn` (VARCHAR, FK -> books.isbn, NOT NULL)
- `student_matricula` (VARCHAR, FK -> students.matricula, NOT NULL)
- `reservation_date` (TIMESTAMP, NOT NULL)
- `queue_position` (INTEGER, NOT NULL) - Posição na fila (1 a 5)
- `status` (VARCHAR, NOT NULL) - ACTIVE, CANCELLED, FULFILLED
- `created_at` (TIMESTAMP, NOT NULL)

### Tabela: `library_settings`

- `id` (BIGINT, PK, sempre 1)
- `loan_period_days` (INTEGER, NOT NULL, padrão: 14)
- `max_loans_per_student` (INTEGER, NOT NULL, padrão: 3)
- `fine_per_day` (INTEGER, NOT NULL, padrão: 100) - Multa por dia de atraso em centavos

## 🔄 Fluxo de Trabalho com Prisma

### 1. Desenvolvimento

```bash
# Criar nova migração após alterar schema.prisma
npx prisma migrate dev --name nome_da_migracao

# Visualizar banco de dados no Prisma Studio
npx prisma studio
```

### 2. Produção

```bash
# Aplicar migrações pendentes
npx prisma migrate deploy

# Gerar Prisma Client
npx prisma generate
```

### 3. Sincronização com JPA

**Importante:** Como o Spring Boot usa JPA/Hibernate com `spring.jpa.hibernate.ddl-auto=update`, as tabelas são criadas automaticamente. Para usar Prisma em conjunto:

1. Configure `spring.jpa.hibernate.ddl-auto=validate` em produção
2. Use Prisma para gerenciar migrações
3. Ou desabilite o auto-ddl e use apenas Prisma

## 🧪 Testando a API

### Usando cURL

```bash
# Criar estudante com e-mail
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "matricula": "2024001",
    "nome": "João Silva",
    "cpf": "12345678901",
    "dataNascimento": "2000-05-15",
    "email": "joao.silva@exemplo.com",
    "telefone": "(11) 99999-1111"
  }'

# Criar empréstimo
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "studentMatricula": "2024001",
    "bookIsbn": "978-8535914093"
  }'

# Criar reserva
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "bookIsbn": "978-8535914093",
    "studentMatricula": "2024001"
  }'

# Enviar notificação de atraso
curl -X POST http://localhost:8080/api/notifications/overdue \
  -H "Content-Type: application/json" \
  -d '{"loanId": 1}'

# Enviar notificação de reserva disponível
curl -X POST http://localhost:8080/api/notifications/reservation-available \
  -H "Content-Type: application/json" \
  -d '{"reservationId": 1}'
```

### Usando Postman/Insomnia

Importe a coleção de endpoints ou crie requisições manualmente seguindo os exemplos acima.

## 📁 Estrutura do Projeto

```
biblioteca-api/
├── src/
│   ├── main/
│   │   ├── java/com/biblioteca/
│   │   │   ├── controller/      # Controllers REST
│   │   │   ├── service/         # Lógica de negócio
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   └── data/structures/ # Estruturas de dados customizadas
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## 🐛 Troubleshooting

### Erro de conexão com banco

- Verifique se o PostgreSQL está rodando
- Confirme credenciais no `application.properties`
- Verifique se o banco `biblioteca_db` existe

### Erro de porta em uso

- Altere `server.port` no `application.properties`
- Ou pare o processo usando a porta 8080

## 📄 Licença

Este projeto é parte de um trabalho acadêmico sobre estruturas de dados.

## 👤 Autor

Desenvolvido como parte do projeto de Estruturas de Dados.
