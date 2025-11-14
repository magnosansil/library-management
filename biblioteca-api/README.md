# Biblioteca API

API REST para gerenciamento de biblioteca desenvolvida em Java com Spring Boot. Esta aplicação implementa funcionalidades de controle de empréstimos utilizando estruturas de dados customizadas.

## 📋 Funcionalidades

### Controle de Empréstimos

- ✅ Verificar disponibilidade de livro antes de emprestar (READ)
- ✅ Atualizar estoque automaticamente
- ✅ Limitar número máximo de empréstimo simultâneo por usuário
- ✅ Notificar bibliotecário sobre empréstimos em atraso
- ✅ Gerar relatório de empréstimos ativos
- ✅ Registrar devolução de livro
- ✅ Registrar novo empréstimo de livro

## 🏗️ Estruturas de Dados Aplicadas

Este projeto utiliza a estrutura de dados **LinkedList** (lista duplamente encadeada) do repositório de estruturas de dados para gerenciar a fila de notificações de empréstimos em atraso no serviço `NotificationService`.

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

**Veja o guia completo em:** [`CLOUD_DATABASE_SETUP.md`](CLOUD_DATABASE_SETUP.md)

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
```

**⚠️ IMPORTANTE**: O arquivo `.env` está no `.gitignore` e NÃO deve ser commitado!

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

| Método | Endpoint                                 | Descrição                                   |
| ------ | ---------------------------------------- | ------------------------------------------- |
| GET    | `/api/loans/books/{bookId}/availability` | Verificar disponibilidade de livro          |
| GET    | `/api/loans/users/{userId}/can-borrow`   | Verificar se usuário pode emprestar         |
| POST   | `/api/loans`                             | Criar novo empréstimo                       |
| PUT    | `/api/loans/{loanId}/return`             | Registrar devolução                         |
| GET    | `/api/loans/active`                      | Listar empréstimos ativos                   |
| GET    | `/api/loans/active/user/{userId}`        | Empréstimos ativos de um usuário            |
| GET    | `/api/loans/check-overdue`               | Verificar e atualizar empréstimos em atraso |
| GET    | `/api/loans/overdue-notifications`       | Obter notificações de empréstimos em atraso |
| GET    | `/api/loans`                             | Listar todos os empréstimos                 |

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
    "userId": 1,
    "bookId": 1
  }'
```

### Verificar Disponibilidade

```bash
curl http://localhost:8080/api/loans/books/1/availability
```

### Registrar Devolução

```bash
curl -X PUT http://localhost:8080/api/loans/1/return
```

### Obter Empréstimos Ativos

```bash
curl http://localhost:8080/api/loans/active
```

### Verificar Empréstimos em Atraso

```bash
curl http://localhost:8080/api/loans/check-overdue
```

## 🗄️ Estrutura do Banco de Dados

### Tabela: `books`

- `id` (BIGINT, PK)
- `title` (VARCHAR, NOT NULL)
- `author` (VARCHAR)
- `publication_date` (DATE)
- `isbn` (VARCHAR)
- `price` (DECIMAL)
- `stock_quantity` (INT, NOT NULL)
- `available_quantity` (INT, NOT NULL)

### Tabela: `users`

- `id` (BIGINT, PK)
- `name` (VARCHAR, NOT NULL)
- `email` (VARCHAR, NOT NULL, UNIQUE)
- `max_loans` (INT, DEFAULT 3)
- `created_at` (TIMESTAMP, NOT NULL)

### Tabela: `loans`

- `id` (BIGINT, PK)
- `user_id` (BIGINT, FK -> users.id)
- `book_id` (BIGINT, FK -> books.id)
- `loan_date` (DATE, NOT NULL)
- `due_date` (DATE, NOT NULL)
- `return_date` (DATE)
- `status` (VARCHAR, NOT NULL) - ACTIVE, RETURNED, OVERDUE
- `created_at` (TIMESTAMP, NOT NULL)

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
# Criar usuário (se tiver endpoint)
# Criar livro (se tiver endpoint)
# Criar empréstimo
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "bookId": 1}'
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
