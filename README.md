# Wisely - Sistema de Gerenciamento de Biblioteca

Sistema completo de gerenciamento de biblioteca com API REST (Spring Boot) e interface web moderna (React + Tailwind CSS).

## 🚀 Visão Geral

O **Wisely** é uma solução completa para gerenciamento de bibliotecas, incluindo controle de empréstimos, reservas, notificações e relatórios estatísticos.

### Principais Funcionalidades

- ✅ **Gerenciamento de Acervo**: Cadastro, edição e busca de livros
- ✅ **Controle de Empréstimos**: Empréstimos, devoluções e cálculo automático de multas
- ✅ **Sistema de Reservas**: Fila ordenada com até 5 posições por livro
- ✅ **Gerenciamento de Alunos**: Cadastro e histórico de atividades
- ✅ **Notificações por E-mail**: Alertas de atrasos e reservas disponíveis
- ✅ **Relatórios e Estatísticas**: 4 tipos de relatórios detalhados
- ✅ **Configurações Flexíveis**: Prazo de devolução, limite de empréstimos e multas personalizáveis

## 🛠️ Stack Tecnológica

### Backend (API)

- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.0** - Framework Java
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **Maven** - Gerenciamento de dependências

### Frontend

- **React 19** - Biblioteca JavaScript
- **Vite** - Build tool e dev server
- **Tailwind CSS 4.1** - Framework CSS utility-first
- **shadcn/ui** - Componentes UI reutilizáveis
- **React Router** - Roteamento
- **Lucide React** - Ícones

## 📦 Pré-requisitos

### Para o Backend

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+ (ou banco na nuvem)

### Para o Frontend

- Node.js 18+ e npm

## 🚀 Instalação Rápida

### 1. Configurar Banco de Dados

**Opção A: Banco na Nuvem (Recomendado)** ⭐

- **Neon**: https://neon.tech (0.5 GB grátis)
- **Supabase**: https://supabase.com (500 MB grátis)
- **Railway**: https://railway.app ($5 crédito grátis/mês)

**Opção B: Docker (Local)**

```bash
docker run --name biblioteca-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=biblioteca_db \
  -p 5432:5432 \
  -d postgres:15
```

### 2. Configurar Backend

```bash
cd biblioteca-api

# Criar arquivo .env na raiz com:
# DATABASE_URL=jdbc:postgresql://seu-host:5432/seu-db
# DB_USERNAME=usuario
# DB_PASSWORD=senha
# MAIL_HOST=smtp.gmail.com
# MAIL_PORT=587
# MAIL_USERNAME=seu-email@gmail.com
# MAIL_PASSWORD=senha-de-app

# Compilar e executar
mvn clean install
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### 3. Configurar Frontend

```bash
cd biblioteca-front

# Instalar dependências
npm install

# Executar em desenvolvimento
npm run dev
```

A aplicação estará disponível em: `http://localhost:5173`

## 📁 Estrutura do Projeto

```
library-management/
├── biblioteca-api/          # Backend (Spring Boot)
│   ├── src/main/java/
│   │   └── com/biblioteca/
│   │       ├── controller/  # Controllers REST
│   │       ├── service/     # Lógica de negócio
│   │       ├── repository/  # Repositórios JPA
│   │       ├── model/       # Entidades JPA
│   │       └── dto/         # Data Transfer Objects
│   └── pom.xml
│
└── biblioteca-front/        # Frontend (React)
    ├── src/
    │   ├── components/      # Componentes React
    │   ├── pages/           # Páginas da aplicação
    │   ├── config/          # Configurações
    │   └── App.jsx          # Componente principal
    └── package.json
```

## 🔌 Principais Endpoints da API

### Empréstimos

- `GET /api/loans` - Listar todos os empréstimos
- `GET /api/loans/active` - Empréstimos ativos
- `GET /api/loans/overdue` - Empréstimos em atraso
- `POST /api/loans` - Criar novo empréstimo
- `PUT /api/loans/{id}/return` - Registrar devolução
- `PUT /api/loans/{id}/fine/paid` - Marcar multa como paga
- `PUT /api/loans/{id}/fine/forgiven` - Marcar multa como perdoada
- `DELETE /api/loans/{id}` - Excluir empréstimo

### Reservas

- `GET /api/reservations` - Listar todas as reservas
- `GET /api/reservations/book/{isbn}` - Reservas ativas de um livro
- `POST /api/reservations` - Criar nova reserva
- `PUT /api/reservations/{id}/fulfill` - Efetivar reserva
- `DELETE /api/reservations/{id}` - Cancelar reserva

### Livros

- `GET /api/books` - Listar todos os livros
- `POST /api/books` - Criar novo livro
- `PUT /api/books/{isbn}` - Atualizar livro
- `DELETE /api/books/{isbn}` - Excluir livro

### Alunos

- `GET /api/students` - Listar todos os alunos
- `POST /api/students` - Criar novo aluno
- `PUT /api/students/{matricula}` - Atualizar aluno
- `DELETE /api/students/{matricula}` - Excluir aluno

### Relatórios

- `GET /api/reports/availability` - Disponibilidade do acervo
- `GET /api/reports/student-metrics` - Métricas de alunos
- `GET /api/reports/loan-statistics` - Estatísticas de empréstimos
- `GET /api/reports/reservation-analytics` - Análise de reservas

### Notificações

- `POST /api/notifications/overdue` - Notificar atraso por e-mail
- `POST /api/notifications/reservation-available` - Notificar reserva disponível

### Configurações

- `GET /api/settings` - Obter configurações
- `PUT /api/settings` - Atualizar configurações

## 📱 Rotas do Frontend

- `/` - Página inicial
- `/acervo` - Lista de livros
- `/livros/novo` - Cadastrar novo livro
- `/livros/:isbn/editar` - Editar livro
- `/alunos` - Lista de alunos
- `/alunos/novo` - Cadastrar novo aluno
- `/emprestimos` - Lista de empréstimos
- `/emprestimos/novo` - Novo empréstimo
- `/reservas` - Lista de reservas ativas
- `/reservas/nova` - Nova reserva
- `/atrasos` - Empréstimos em atraso
- `/relatorios` - Relatórios e estatísticas
- `/configuracoes` - Configurações do sistema

## 🎨 Design Mobile First

A interface foi desenvolvida com abordagem mobile first, garantindo experiência otimizada em dispositivos móveis e adaptável para telas maiores.

## 📊 Funcionalidades Avançadas

### Sistema de Multas

- Cálculo automático de multas por dias de atraso
- Status de multa: PENDING, PAID, FORGIVEN
- Configuração flexível de valor por dia

### Sistema de Reservas

- Fila ordenada com até 5 posições por livro
- Reorganização automática ao cancelar/efetivar
- Contagem automática de reservas ativas

### Atualização Automática de Status

- Status de empréstimos atualizado automaticamente baseado em datas
- Cálculo automático de dias de atraso e multas
- Endpoint `/api/loans/check-overdue` para atualização manual

### Notificações por E-mail

- Configuração SMTP (Gmail, etc.)
- Notificações de livros em atraso
- Notificações de reservas disponíveis

## 🗄️ Estrutura do Banco de Dados

### Principais Tabelas

- **books**: Livros do acervo (ISBN, título, autor, quantidade, etc.)
- **students**: Alunos cadastrados (matrícula, nome, CPF, e-mail, etc.)
- **loans**: Empréstimos (status, datas, multas, etc.)
- **reservations**: Reservas (status, posição na fila, etc.)
- **library_settings**: Configurações globais (prazo, limite, multa)

Para mais detalhes, consulte a documentação em `biblioteca-api/README.md`.

## 🧪 Testando a API

### Exemplo: Criar um Empréstimo

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "studentMatricula": "2024001",
    "bookIsbn": "978-8535914093"
  }'
```

### Exemplo: Obter Relatório de Disponibilidade

```bash
curl http://localhost:8080/api/reports/availability
```

## 📚 Documentação Adicional

- **Backend**: `biblioteca-api/README.md` - Documentação completa da API
- **Frontend**: `biblioteca-front/README.md` - Documentação do frontend
- **Rotas da API**: `biblioteca-api/ROUTES.md` - Lista completa de endpoints
- **Como Rodar**: `biblioteca-api/RODAR_API.md` - Guia de execução

## 🐛 Troubleshooting

### Erro de conexão com banco

- Verifique se o PostgreSQL está rodando
- Confirme credenciais no `.env` ou `application.properties`
- Verifique se o banco `biblioteca_db` existe

### Erro de porta em uso

- Backend: Altere `server.port` no `application.properties`
- Frontend: Altere `server.port` no `vite.config.js`

### Erro ao instalar dependências do frontend

```bash
# Limpar cache e reinstalar
rm -rf node_modules package-lock.json
npm install
```

## 📄 Licença

Este projeto é parte de um trabalho acadêmico sobre estruturas de dados.

## 👤 Autor

Desenvolvido como parte do projeto da disciplina Estruturas de Dados do Instituto Federal da Bahia - Semestre 2025.2.

---

**Desenvolvido com ❤️ usando Spring Boot e React**
