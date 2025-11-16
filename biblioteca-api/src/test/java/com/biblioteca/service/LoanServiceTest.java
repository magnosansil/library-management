package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.dto.LoanReturnDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Loan;
import com.biblioteca.model.LibrarySettings;
import com.biblioteca.model.Student;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LibrarySettingsRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o serviço de empréstimos
 * Testa funcionalidades básicas, status automático, multas e cálculos
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LibrarySettingsRepository settingsRepository;

    private Student testStudent;
    private Book testBook;
    private LibrarySettings settings;

    @BeforeEach
    public void setUp() {
        // Limpar dados anteriores
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        studentRepository.deleteAll();

        // Criar e salvar configurações padrão
        settings = new LibrarySettings();
        settings.setId(1L);
        settings.setLoanPeriodDays(14);
        settings.setMaxLoansPerStudent(3);
        settings.setFinePerDay(100);
        settingsRepository.save(settings);

        // Criar e salvar aluno de teste
        testStudent = new Student();
        testStudent.setMatricula("MAT001");
        testStudent.setNome("João Silva");
        testStudent.setCpf("12345678901");
        testStudent.setDataNascimento(LocalDate.of(2000, 1, 1));
        testStudent.setEmail("joao@test.com");
        testStudent.setTelefone("11999999999");
        testStudent.setReservationsCount(0);
        studentRepository.save(testStudent);

        // Criar e salvar livro de teste
        testBook = new Book();
        testBook.setIsbn("978-1234567890");
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setQuantity(5);
        testBook.setActiveReservationsCount(0);
        bookRepository.save(testBook);
    }

    // ======================== TESTES DE EMPRÉSTIMOS BÁSICOS ========================

    @Test
    public void testCreateLoanDecrementsStock() {
        // Arrange
        int initialQuantity = testBook.getQuantity();
        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(testStudent.getMatricula());

        // Act
        LoanResponseDTO response = loanService.createLoan(request);

        // Assert
        assertNotNull(response);
        Book updatedBook = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(updatedBook);
        assertEquals(initialQuantity - 1, updatedBook.getQuantity());
    }

    @Test
    public void testCreateLoanSetsDueDateCorrectly() {
        // Arrange
        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(testStudent.getMatricula());

        // Act
        LoanResponseDTO response = loanService.createLoan(request);

        // Assert
        assertNotNull(response);
        Loan loan = loanRepository.findById(response.getId()).orElse(null);
        assertNotNull(loan);

        // Due date deve ser 14 dias após a data de empréstimo
        long daysUntilDue = java.time.Duration.between(
                loan.getLoanDate(),
                loan.getDueDate()
        ).toDays();
        assertEquals(14, daysUntilDue);
    }

    @Test
    public void testCreateLoanStatusIsActive() {
        // Arrange
        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(testStudent.getMatricula());

        // Act
        LoanResponseDTO response = loanService.createLoan(request);

        // Assert
        assertNotNull(response);
        assertEquals(Loan.LoanStatus.ACTIVE, response.getStatus());
    }

    // ======================== TESTES DE DEVOLUÇÃO ========================

    @Test
    public void testReturnLoanIncrementsStock() {
        // Arrange
        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        int stockAfterLoan = testBook.getQuantity() - 1;

        // Act
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Assert
        Book updatedBook = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(updatedBook);
        assertEquals(stockAfterLoan + 1, updatedBook.getQuantity());
    }

    @Test
    public void testReturnLoanMarksAsReturned() {
        // Arrange
        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        // Act
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Assert
        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
    }

    @Test
    public void testReturnLoanWithoutDelayHasZeroFine() {
        // Arrange
        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        // Act - Devolver no mesmo dia
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Assert
        assertEquals(0, returnedLoan.getOverdueDays());
        assertEquals(0, returnedLoan.getFineAmount());
    }

    // ======================== TESTES DE ATRASO E MULTA ========================

    @Test
    public void testReturnLoanWithDelayCalculatesFineCorrectly() {
        // Arrange
        LocalDateTime loanDate = LocalDateTime.now().minusDays(20);
        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        createRequest.setLoanDate(loanDate);
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        // Due date será 14 dias após loan date (em 6 dias atrás)
        // Então há 20 - 14 = 6 dias de atraso
        LocalDateTime returnDate = LocalDateTime.now();

        // Act
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        returnRequest.setReturnDate(returnDate);
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Assert
        assertTrue(returnedLoan.getOverdueDays() > 0);
        // Fine = overdueDays * finePerDay
        assertEquals(returnedLoan.getOverdueDays() * 100, returnedLoan.getFineAmount());
    }

    @Test
    public void testReturnLoanWithCustomDateCalculatesFineWithCustomDate() {
        // Arrange
        LocalDateTime loanDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime customReturnDate = LocalDateTime.of(2024, 1, 20, 10, 0, 0);
        // Due date: 2024-01-15 (14 dias após 2024-01-01)
        // Return date: 2024-01-20
        // Overdue: 5 dias
        // Expected fine: 5 * 100 = 500 centavos

        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        createRequest.setLoanDate(loanDate);
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        // Act
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        returnRequest.setReturnDate(customReturnDate);
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Assert
        assertEquals(5, returnedLoan.getOverdueDays());
        assertEquals(500, returnedLoan.getFineAmount());
    }

    @Test
    public void testFineCalculationUsesSettingsFinePerDay() {
        // Arrange
        // Mudar a multa por dia para 200 centavos
        settings.setFinePerDay(200);
        settingsRepository.save(settings);

        LocalDateTime loanDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime customReturnDate = LocalDateTime.of(2024, 1, 20, 10, 0, 0);

        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        createRequest.setLoanDate(loanDate);
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        // Act
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        returnRequest.setReturnDate(customReturnDate);
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Assert - 5 dias de atraso * 200 centavos = 1000
        assertEquals(5, returnedLoan.getOverdueDays());
        assertEquals(1000, returnedLoan.getFineAmount());
    }

    // ======================== TESTES DE STATUS AUTOMÁTICO ========================

    @Test
    public void testActiveLoanBecomesOverdueAfterDueDate() {
        // Arrange
        LocalDateTime loanDate = LocalDateTime.now().minusDays(20);
        LoanRequestDTO createRequest = new LoanRequestDTO();
        createRequest.setBookIsbn(testBook.getIsbn());
        createRequest.setStudentMatricula(testStudent.getMatricula());
        createRequest.setLoanDate(loanDate);
        LoanResponseDTO loanResponse = loanService.createLoan(createRequest);

        // Act
        Loan loan = loanRepository.findById(loanResponse.getId()).orElse(null);
        assertNotNull(loan);
        boolean statusChanged = loanService.updateLoanStatus(loan);

        // Assert
        assertTrue(statusChanged);
        assertEquals(Loan.LoanStatus.OVERDUE, loan.getStatus());
    }

    @Test
    public void testGetActiveLoansReturnsOnlyActive() {
        // Arrange - Criar 3 empréstimos, devolver 1
        LoanRequestDTO request1 = new LoanRequestDTO();
        request1.setBookIsbn(testBook.getIsbn());
        request1.setStudentMatricula(testStudent.getMatricula());

        LoanResponseDTO loan1 = loanService.createLoan(request1);

        // Criar outro livro para segundo empréstimo
        Book book2 = new Book();
        book2.setIsbn("978-1234567891");
        book2.setTitle("Test Book 2");
        book2.setAuthor("Test Author 2");
        book2.setQuantity(5);
        book2.setActiveReservationsCount(0);
        bookRepository.save(book2);

        LoanRequestDTO request2 = new LoanRequestDTO();
        request2.setBookIsbn(book2.getIsbn());
        request2.setStudentMatricula(testStudent.getMatricula());

        LoanResponseDTO loan2 = loanService.createLoan(request2);

        // Devolver primeiro
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        loanService.returnLoan(loan1.getId(), returnRequest);

        // Act
        List<LoanResponseDTO> activeLoans = loanService.getActiveLoans();

        // Assert
        assertEquals(1, activeLoans.size());
        assertEquals(loan2.getId(), activeLoans.get(0).getId());
    }

    @Test
    public void testGetOverdueLoansReturnsOnlyOverdue() {
        // Arrange
        LocalDateTime pastLoanDate = LocalDateTime.now().minusDays(20);
        LoanRequestDTO overdueRequest = new LoanRequestDTO();
        overdueRequest.setBookIsbn(testBook.getIsbn());
        overdueRequest.setStudentMatricula(testStudent.getMatricula());
        overdueRequest.setLoanDate(pastLoanDate);
        LoanResponseDTO overdueLoan = loanService.createLoan(overdueRequest);

        // Act
        List<LoanResponseDTO> overdueLoans = loanService.getOverdueLoans();

        // Assert
        assertTrue(overdueLoans.size() > 0);
        assertTrue(overdueLoans.stream().anyMatch(l -> l.getId().equals(overdueLoan.getId())));
    }

    @Test
    public void testGetReturnedLoansReturnsOnlyReturned() {
        // Arrange
        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(testStudent.getMatricula());
        LoanResponseDTO loanResponse = loanService.createLoan(request);

        LoanReturnDTO returnRequest = new LoanReturnDTO();
        loanService.returnLoan(loanResponse.getId(), returnRequest);

        // Act
        List<LoanResponseDTO> returnedLoans = loanService.getReturnedLoans();

        // Assert
        assertTrue(returnedLoans.size() > 0);
        assertTrue(returnedLoans.stream().anyMatch(l -> l.getId().equals(loanResponse.getId())));
    }

    @Test
    public void testGetActiveAndOverdueLoansReturnsBoth() {
        // Arrange
        LoanRequestDTO activeRequest = new LoanRequestDTO();
        activeRequest.setBookIsbn(testBook.getIsbn());
        activeRequest.setStudentMatricula(testStudent.getMatricula());
        LoanResponseDTO activeLoan = loanService.createLoan(activeRequest);

        Book book2 = new Book();
        book2.setIsbn("978-1234567891");
        book2.setTitle("Test Book 2");
        book2.setAuthor("Test Author 2");
        book2.setQuantity(5);
        book2.setActiveReservationsCount(0);
        bookRepository.save(book2);

        LocalDateTime pastLoanDate = LocalDateTime.now().minusDays(20);
        LoanRequestDTO overdueRequest = new LoanRequestDTO();
        overdueRequest.setBookIsbn(book2.getIsbn());
        overdueRequest.setStudentMatricula(testStudent.getMatricula());
        overdueRequest.setLoanDate(pastLoanDate);
        LoanResponseDTO overdueLoan = loanService.createLoan(overdueRequest);

        // Act
        List<LoanResponseDTO> activeAndOverdue = loanService.getActiveAndOverdueLoans();

        // Assert
        assertTrue(activeAndOverdue.size() >= 2);
        assertTrue(activeAndOverdue.stream().anyMatch(l -> l.getId().equals(activeLoan.getId())));
        assertTrue(activeAndOverdue.stream().anyMatch(l -> l.getId().equals(overdueLoan.getId())));
    }

    // ======================== TESTES DE DISPONIBILIDADE ========================

    @Test
    public void testIsBookAvailableReturnsTrueWhenQuantityGreaterThanZero() {
        // Arrange
        testBook.setQuantity(5);
        bookRepository.save(testBook);

        // Act
        boolean isAvailable = loanService.isBookAvailable(testBook.getIsbn());

        // Assert
        assertTrue(isAvailable);
    }

    @Test
    public void testIsBookAvailableReturnsFalseWhenOutOfStock() {
        // Arrange
        testBook.setQuantity(0);
        bookRepository.save(testBook);

        // Act
        boolean isAvailable = loanService.isBookAvailable(testBook.getIsbn());

        // Assert
        assertFalse(isAvailable);
    }

    @Test
    public void testCanStudentBorrowReturnsTrueWhenUnderLimit() {
        // Arrange
        // Student pode pegar até 3 (limite padrão)

        // Act
        boolean canBorrow = loanService.canStudentBorrow(testStudent.getMatricula());

        // Assert
        assertTrue(canBorrow);
    }

    @Test
    public void testCanStudentBorrowReturnsFalseWhenAtLimit() {
        // Arrange
        settings.setMaxLoansPerStudent(1);
        settingsRepository.save(settings);

        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(testStudent.getMatricula());
        loanService.createLoan(request);

        // Act
        boolean canBorrow = loanService.canStudentBorrow(testStudent.getMatricula());

        // Assert
        assertFalse(canBorrow);
    }

    @Test
    public void testGetActiveLoansByStudent() {
        // Arrange
        LoanRequestDTO request = new LoanRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(testStudent.getMatricula());
        loanService.createLoan(request);

        // Act
        List<LoanResponseDTO> studentLoans = loanService.getActiveLoansByStudent(testStudent.getMatricula());

        // Assert
        assertTrue(studentLoans.size() > 0);
        assertTrue(studentLoans.stream()
                .allMatch(l -> l.getStudentMatricula().equals(testStudent.getMatricula())));
    }
}
