# 🚀 Como Rodar a API e Manter em Execução

## 1. Executar a Aplicação

### Opção A: Terminal Normal (Recomendado para testes)

Abra um terminal PowerShell/CMD e execute:

```powershell
cd biblioteca-api
mvn spring-boot:run
```

**A aplicação ficará rodando até você pressionar `Ctrl+C`**

### Opção B: Background (Windows PowerShell)

Para rodar em background e continuar usando o terminal:

```powershell
cd biblioteca-api
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvn spring-boot:run"
```

### Opção C: Usando o JAR compilado

```powershell
# Compilar
mvn clean package -DskipTests

# Executar
java -jar target/biblioteca-api-1.0.0.jar
```

## 2. Verificar se Está Rodando

### Verificar Health Check (Conexão com Banco)

Abra outro terminal e execute:

```powershell
# Verificar se API está respondendo
curl http://localhost:8080/api/health

# Ou no navegador:
# http://localhost:8080/api/health
```

**Resposta esperada:**

```json
{
  "status": "UP",
  "timestamp": 1234567890,
  "database": {
    "status": "CONNECTED",
    "database": "PostgreSQL",
    "version": "15.x",
    "url": "jdbc:postgresql://...",
    "driver": "PostgreSQL JDBC Driver"
  }
}
```

### Verificar Porta

```powershell
netstat -ano | findstr :8080
```

Se aparecer algo como `TCP 0.0.0.0:8080`, a aplicação está rodando!

## 3. Testar Endpoints

### Usando PowerShell (test-api.ps1)

```powershell
cd biblioteca-api
.\test-api.ps1
```

### Usando cURL Manual

```powershell
# Health check
curl http://localhost:8080/api/health

# Listar alunos
curl http://localhost:8080/api/students

# Listar livros
curl http://localhost:8080/api/books

# Listar empréstimos
curl http://localhost:8080/api/loans

# Obter configurações globais
curl http://localhost:8080/api/settings
```

### Usando Insomnia/Postman

1. **Base URL**: `http://localhost:8080/api`
2. **Health Check**: `GET http://localhost:8080/api/health`
3. **Criar Aluno**: `POST http://localhost:8080/api/students`
   ```json
   {
     "matricula": "2024001",
     "nome": "João Silva",
     "cpf": "12345678901",
     "dataNascimento": "2000-05-15"
   }
   ```
4. **Criar Livro**: `POST http://localhost:8080/api/books`
   ```json
   {
     "isbn": "978-8535914093",
     "title": "Dom Casmurro",
     "author": "Machado de Assis",
     "quantity": 5
   }
   ```
5. **Criar Empréstimo**: `POST http://localhost:8080/api/loans`
   ```json
   {
     "studentMatricula": "2024001",
     "bookIsbn": "978-8535914093"
   }
   ```
6. **Devolver Livro (com cálculo automático de multa)**:
   - `PUT http://localhost:8080/api/loans/{loanId}/return` (usa data atual)
   - `PUT http://localhost:8080/api/loans/{loanId}/return` com body: `{"returnDate": "2024-01-20T14:30:00"}` (data específica)
7. **Obter Configurações**: `GET http://localhost:8080/api/settings`
8. **Atualizar Configurações**: `PUT http://localhost:8080/api/settings`
   ```json
   {
     "loanPeriodDays": 14,
     "maxLoansPerStudent": 3,
     "finePerDay": 100
   }
   ```

## 4. Logs e Debug

### Ver Logs da Aplicação

Quando rodar `mvn spring-boot:run`, você verá:

- ✅ "Started BibliotecaApplication" = Aplicação iniciou
- ✅ "Hibernate: create table..." = Tabelas sendo criadas
- ✅ "Variáveis do arquivo .env carregadas" = Configuração OK

### Verificar Conexão com Banco

Os logs mostrarão:

```
Hibernate: create table books ...
Hibernate: create table users ...
Hibernate: create table loans ...
```

Se aparecer erro de conexão, verifique:

1. Arquivo `.env` existe e tem as credenciais corretas
2. Banco Neon está ativo (não pausado)
3. Connection string está correta

## 5. Manter Rodando para Testes

### Para Insomnia/Postman

1. Execute `mvn spring-boot:run` em um terminal
2. **Deixe esse terminal aberto** (não feche!)
3. Use outro terminal ou o Insomnia/Postman para fazer requisições
4. Para parar: Pressione `Ctrl+C` no terminal onde está rodando

### Para Desenvolvimento Contínuo

O Spring Boot DevTools está configurado, então:

- Alterações no código reiniciam automaticamente
- Não precisa parar e iniciar manualmente

## 6. Troubleshooting

### Aplicação não inicia

1. Verifique se a porta 8080 está livre:
   ```powershell
   netstat -ano | findstr :8080
   ```
2. Se estiver em uso, mude a porta no `.env`:
   ```
   SERVER_PORT=8081
   ```

### Erro de conexão com banco

1. Verifique o arquivo `.env`:
   ```powershell
   cat .env
   ```
2. Teste a connection string manualmente
3. Verifique se o banco Neon está ativo (não pausado)

### Build Success mas não roda

Execute com mais verbosidade:

```powershell
mvn spring-boot:run -X
```

Ou verifique os logs completos no console.

## 7. Endpoints Disponíveis

### Sistema

- `GET /api/health` - Health check com status do banco
- `GET /api/routes` - Listar todas as rotas da API

### Alunos

- `GET /api/students` - Listar alunos
- `POST /api/students` - Criar aluno
- `POST /api/students/batch` - Criar múltiplos alunos
- `GET /api/students/{matricula}` - Buscar aluno por matrícula
- `PUT /api/students/{matricula}` - Atualizar aluno
- `DELETE /api/students/{matricula}` - Excluir aluno

### Livros

- `GET /api/books` - Listar livros
- `POST /api/books` - Criar livro
- `POST /api/books/batch` - Criar múltiplos livros
- `GET /api/books/{isbn}` - Buscar livro por ISBN
- `PUT /api/books/{isbn}` - Atualizar livro
- `DELETE /api/books/{isbn}` - Excluir livro

### Empréstimos

- `GET /api/loans` - Listar todos os empréstimos
- `GET /api/loans/active` - Empréstimos ativos
- `GET /api/loans/overdue` - Empréstimos em atraso
- `GET /api/loans/returned` - Empréstimos devolvidos
- `GET /api/loans/active-and-overdue` - Empréstimos ativos e em atraso
- `GET /api/loans/active/student/{matricula}` - Empréstimos ativos de um aluno
- `POST /api/loans` - Criar empréstimo
- `PUT /api/loans/{loanId}/return` - Devolver livro (calcula multa automaticamente)
  - Body opcional: `{"returnDate": "2024-01-20T14:30:00"}`

### Reservas

- `GET /api/reservations` - Listar todas as reservas
- `GET /api/reservations/{id}` - Buscar reserva por ID
- `GET /api/reservations/book/{isbn}` - Reservas ativas de um livro (ordem da fila)
- `GET /api/reservations/student/{matricula}` - Reservas ativas de um estudante
- `POST /api/reservations` - Criar reserva (máximo 5 por livro)
- `DELETE /api/reservations/{id}` - Cancelar reserva (reorganiza fila)
- `PUT /api/reservations/{id}/fulfill` - Efetivar reserva (marcar como gerou empréstimo)

### Configurações

- `GET /api/settings` - Obter configurações globais
- `PUT /api/settings` - Atualizar configurações globais (prazo, limite, multa)

Veja `API_DOCUMENTATION.md` e `ROUTES.md` para documentação completa.
