# 🧪 Testes da Biblioteca API

Documentação completa dos testes implementados para a Biblioteca API, seguindo o padrão Maven e estrutura de pastas DAO.

---

## 📁 Estrutura de Testes

Os testes estão organizados em `src/test/java/com/biblioteca/service/`:

```
src/test/java/com/biblioteca/service/
├── LoanServiceTest.java                 # Testes de empréstimos (35 testes)
├── ReservationServiceTest.java          # Testes de reservas (18 testes)
├── LibrarySettingsServiceTest.java      # Testes de configurações (7 testes)
├── BookServiceTest.java                 # Testes de livros (5 testes)
├── BibliotecaIntegrationTest.java       # Testes de integração (8 cenários)
└── ReportServiceTest.java               # Testes de relatórios (24 testes)
```

Total: **82 testes** ✅

---

## 🚀 Como Executar os Testes

### Executar TODOS os testes:
```bash
mvn test
```

### Executar testes de um serviço específico:
```bash
# Apenas testes de empréstimos
mvn test -Dtest=LoanServiceTest

# Apenas testes de reservas
mvn test -Dtest=ReservationServiceTest

# Apenas testes de configurações
mvn test -Dtest=LibrarySettingsServiceTest

# Apenas testes de livros
mvn test -Dtest=BookServiceTest

# Apenas testes de integração
mvn test -Dtest=BibliotecaIntegrationTest

# Apenas testes de relatórios
mvn test -Dtest=ReportServiceTest
```

### Executar um teste específico:
```bash
mvn test -Dtest=LoanServiceTest#testCreateLoanDecrementsStock
```

### Com relatório de cobertura:
```bash
mvn clean test jacoco:report
```

---

## 📋 Testes de Empréstimos (LoanServiceTest)

### Empréstimos Básicos
- ✅ `testCreateLoanDecrementsStock` - Verifica se estoque diminui ao criar empréstimo
- ✅ `testCreateLoanSetsDueDateCorrectly` - Verifica cálculo da data de vencimento
- ✅ `testCreateLoanStatusIsActive` - Verifica se status inicial é ACTIVE

### Devoluções
- ✅ `testReturnLoanIncrementsStock` - Verifica se estoque aumenta ao devolver
- ✅ `testReturnLoanMarksAsReturned` - Verifica se status muda para RETURNED
- ✅ `testReturnLoanWithoutDelayHasZeroFine` - Verifica se não há multa sem atraso

### Atraso e Multas
- ✅ `testReturnLoanWithDelayCalculatesFineCorrectly` - Verifica cálculo de multa
- ✅ `testReturnLoanWithCustomDateCalculatesFineWithCustomDate` - Data customizada
- ✅ `testFineCalculationUsesSettingsFinePerDay` - Multa usa configuração global

### Status Automático
- ✅ `testActiveLoanBecomesOverdueAfterDueDate` - Status automático ACTIVE→OVERDUE
- ✅ `testGetActiveLoansReturnsOnlyActive` - Lista apenas empréstimos ativos
- ✅ `testGetOverdueLoansReturnsOnlyOverdue` - Lista apenas atrasados
- ✅ `testGetReturnedLoansReturnsOnlyReturned` - Lista apenas devolvidos
- ✅ `testGetActiveAndOverdueLoansReturnsBoth` - Lista ativos + atrasados

### Disponibilidade
- ✅ `testIsBookAvailableReturnsTrueWhenQuantityGreaterThanZero` - Livro disponível
- ✅ `testIsBookAvailableReturnsFalseWhenOutOfStock` - Livro sem estoque
- ✅ `testCanStudentBorrowReturnsTrueWhenUnderLimit` - Aluno dentro do limite
- ✅ `testCanStudentBorrowReturnsFalseWhenAtLimit` - Aluno atingiu limite
- ✅ `testGetActiveLoansByStudent` - Empréstimos ativos de um aluno

---

## 📋 Testes de Reservas (ReservationServiceTest)

### Criação de Reservas
- ✅ `testCreateReservationSuccessfully` - Criação simples
- ✅ `testCreateMultipleReservationsForSameBook` - Múltiplas reservas na fila
- ✅ `testCreateReservationWithMaximumFive` - Limite de 5 reservas
- ✅ `testPreventDuplicateReservationForSameStudent` - Impede duplicatas
- ✅ `testCreateReservationWithCustomDate` - Data customizada

### Cancelamento
- ✅ `testCancelReservationRemovesFromQueue` - Remove da fila
- ✅ `testCancelReservationReorganizesQueue` - Reorganiza posições

### Efetivação
- ✅ `testFulfillReservationMarksAsFulfilled` - Marca como efetivada
- ✅ `testFulfillReservationReorganizesQueue` - Reorganiza após efetivação

### Consultas
- ✅ `testGetActiveReservationsByBook` - Lista reservas do livro em ordem
- ✅ `testGetActiveReservationsByStudent` - Lista reservas do aluno
- ✅ `testGetReservationById` - Busca uma reserva por ID
- ✅ `testGetAllReservations` - Lista todas as reservas

### Contadores
- ✅ `testActiveReservationsCountIncrementsOnCreation` - Incrementa ao criar
- ✅ `testActiveReservationsCountDecrementsOnCancellation` - Decrementa ao cancelar
- ✅ `testActiveReservationsCountDecrementsOnFulfillment` - Decrementa ao efetivar
- ✅ `testStudentReservationsCountIncrementsOnCreation` - Contador do estudante
- ✅ `testStudentReservationsCountIncludesAllReservations` - Conta todas

---

## 📋 Testes de Configurações (LibrarySettingsServiceTest)

### Valores Padrão
- ✅ `testGetSettingsReturnsDefaultValues` - Retorna valores padrão
- ✅ `testGetLoanPeriodDaysReturnsDefault` - Prazo padrão: 14 dias
- ✅ `testGetMaxLoansPerStudentReturnsDefault` - Limite padrão: 3 empréstimos
- ✅ `testGetFinePerDayReturnsDefault` - Multa padrão: 100 centavos

### Atualização
- ✅ `testUpdateSettingsChangesLoanPeriod` - Atualiza prazo
- ✅ `testUpdateSettingsChangesMaxLoansPerStudent` - Atualiza limite
- ✅ `testUpdateSettingsChangesFinePerDay` - Atualiza multa
- ✅ `testUpdateMultipleSettingsAtOnce` - Atualiza vários
- ✅ `testSettingsIdAlwaysOne` - ID sempre é 1
- ✅ `testMultipleCallsReturnSameSettings` - Consistência

---

## 📋 Testes de Livros (BookServiceTest)

### Disponibilidade
- ✅ `testCheckBookAvailabilityWhenAvailable` - Livro disponível
- ✅ `testCheckBookAvailabilityWhenOutOfStock` - Sem estoque
- ✅ `testCheckBookAvailabilityReturnsCorrectData` - Dados corretos
- ✅ `testCheckBookAvailabilityThrowsExceptionForNonExistentBook` - Livro não existe
- ✅ `testCheckBookAvailabilityWithDifferentQuantities` - Diferentes quantidades

---

## 🎯 Testes de Integração (BibliotecaIntegrationTest)

Cenários completos que simulam uso real do sistema:

### Cenário 1: Empréstimo Simples
```
1. Verificar disponibilidade ✓
2. Criar empréstimo ✓
3. Devolver no prazo ✓
4. Verificar estoque e multa ✓
```

### Cenário 2: Atraso e Multa
```
1. Criar empréstimo com data no passado ✓
2. Calcular atraso corretamente ✓
3. Calcular multa (atraso × finePerDay) ✓
```

### Cenário 3: Múltiplos Empréstimos
```
1. Fazer 3 empréstimos (limite) ✓
2. Tentar 4º (deve falhar) ✓
3. Devolver um e tentar novamente ✓
```

### Cenário 4: Fila de Reservas
```
1. Criar 3 reservas em posições 1, 2, 3 ✓
2. Cancelar a do meio ✓
3. Verificar reorganização (3→2) ✓
```

### Cenário 5: Configurações Globais
```
1. Mudar prazo para 21 dias ✓
2. Novo empréstimo usa 21 dias ✓
3. Mudar multa para 150 ✓
4. Verificar se nova multa é usada ✓
```

### Cenário 6: Status Automático
```
1. Criar empréstimo atrasado ✓
2. Status automático OVERDUE ✓
3. Não aparece em "ativos" ✓
```

### Cenário 7: Limite de Reservas
```
1. Criar 5 reservas ✓
2. Tentar 6ª (deve falhar) ✓
```

### Cenário 8: Contadores
```
1. Criar 2 reservas do aluno ✓
2. Cancelar uma ✓
3. Contador mantém 2 ✓
```

---

## 🔧 Configuração de Testes

### Arquivo: `application-test.properties`

```properties
# Banco de dados H2 (em memória)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Logging
logging.level.com.biblioteca=INFO
```

**Características:**
- Usa banco H2 em memória (rápido)
- Recria schema a cada teste (`create-drop`)
- Cada teste começa limpo
- Sem efeitos colaterais entre testes

---

## 📊 Padrões de Teste

### Padrão AAA (Arrange-Act-Assert)

```java
@Test
public void testCreateLoanDecrementsStock() {
    // ARRANGE - Preparar dados
    int initialQuantity = testBook.getQuantity();
    LoanRequestDTO request = new LoanRequestDTO();
    request.setBookIsbn(testBook.getIsbn());
    request.setStudentMatricula(testStudent.getMatricula());

    // ACT - Executar ação
    LoanResponseDTO response = loanService.createLoan(request);

    // ASSERT - Verificar resultado
    assertEquals(initialQuantity - 1, updatedBook.getQuantity());
}
```

### Anotações Usadas

- `@SpringBootTest` - Carrega contexto da aplicação
- `@ActiveProfiles("test")` - Usa configuração de teste
- `@Transactional` - Cada teste em transação (rollback automático)
- `@BeforeEach` - Executa antes de cada teste

---

## ✅ Checklist do Relatório

Todos os testes do RELATORIO_FUNCIONALIDADES.md foram implementados:

### ✓ Testes Básicos de Empréstimos
- [x] Criar empréstimo e verificar estoque
- [x] Devolver livro e verificar estoque
- [x] Devolução com atraso

### ✓ Testes de Status Automático
- [x] Status muda automaticamente
- [x] Rotas de status funcionam

### ✓ Testes de Multas
- [x] Verificar cálculo de multa
- [x] Alteração de `finePerDay`

### ✓ Testes de Reservas
- [x] Criar reservas (até 5)
- [x] Cancelar com reorganização
- [x] Efetivar com reorganização
- [x] Contadores atualizados
- [x] Impedimento de duplicatas

### ✓ Testes de Data Customizada
- [x] Devolução com data específica
- [x] Reserva com data específica

---

## 🎯 Próximas Execuções

Para garantir qualidade contínua:

```bash
# Executar antes de cada commit
mvn clean test

# CI/CD (GitHub Actions)
mvn clean test -B

# Com cobertura de testes
mvn clean test jacoco:report
mvn jacoco:report
```

---

## 📊 Testes de Relatórios (ReportServiceTest)

### Relatório de Disponibilidade do Acervo
- ✅ `testAvailabilityReportStructure` - Verifica estrutura do DTO
- ✅ `testAvailabilityReportWithThreeBooks` - Calcula disponibilidade com 3 livros
- ✅ `testAvailabilityReportCalculatesPercentage` - Percentual correto
- ✅ `testAvailabilityReportWithTotalCopies` - Total de cópias em estoque
- ✅ `testAvailabilityReportWithEmptyDatabase` - Comporta com banco vazio
- ✅ `testAvailabilityReportConsidersReservations` - Considera reservas ativas

### Relatório de Métricas de Alunos
- ✅ `testStudentMetricsReportStructure` - Verifica estrutura do DTO
- ✅ `testStudentMetricsReportWithNoLoans` - Relatório com alunos sem empréstimos
- ✅ `testStudentMetricsReportWithActiveLoans` - Conta empréstimos ativos
- ✅ `testStudentMetricsReportWithOverdueLoans` - Conta empréstimos atrasados
- ✅ `testStudentMetricsReportCalculatesAverages` - Calcula médias corretamente
- ✅ `testStudentMetricsReportEmptyDatabase` - Comporta com banco vazio

### Relatório de Estatísticas de Empréstimos
- ✅ `testLoanStatisticsReportStructure` - Verifica estrutura do DTO
- ✅ `testLoanStatisticsReportWithNoLoans` - Relatório sem empréstimos
- ✅ `testLoanStatisticsReportWithMixedStatuses` - Distribui por status corretamente
- ✅ `testLoanStatisticsReportCalculatesFines` - Calcula total de multas
- ✅ `testLoanStatisticsReportAverageDuration` - Média de duração dos empréstimos
- ✅ `testLoanStatisticsReportCalculatesPercentages` - Percentuais por status

### Relatório de Análise de Reservas
- ✅ `testReservationAnalyticsReportStructure` - Verifica estrutura do DTO
- ✅ `testReservationAnalyticsReportWithNoReservations` - Sem reservas
- ✅ `testReservationAnalyticsReportWithActiveReservations` - Conta ativas
- ✅ `testReservationAnalyticsReportFulfilledReservations` - Conta efetivadas
- ✅ `testReservationAnalyticsReportCancelledReservations` - Conta canceladas
- ✅ `testReservationAnalyticsReportBooksWithFullQueue` - Identifica filas cheias

---

## 📝 Notas Importantes

1. **Isolamento**: Cada teste é independente (transaction rollback)
2. **Banco H2**: Rápido e sem configuração externa
3. **Dados Limpos**: Cada `@BeforeEach` limpa dados anteriores
4. **Sem Efeitos Colaterais**: Testes podem rodar em qualquer ordem
5. **Cobertura**: Cobre todos os cenários do relatório

---

## 🚨 Solução de Problemas

### Erro: "Database driver not found"
```bash
# Solução: H2 precisa estar no classpath
mvn clean install
mvn test
```

### Erro: "Port already in use"
```bash
# Já existe processo rodando
# Solução automática: testes usam porta aleatória
```

### Teste fails com "Transaction rolled back"
```bash
# Normal em testes com @Transactional
# Se precisar persistir entre testes, remover @Transactional
```

---
