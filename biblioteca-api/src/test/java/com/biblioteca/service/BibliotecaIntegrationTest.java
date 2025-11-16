package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.dto.LoanReturnDTO;
import com.biblioteca.dto.ReservationRequestDTO;
import com.biblioteca.dto.ReservationResponseDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Loan;
import com.biblioteca.model.LibrarySettings;
import com.biblioteca.model.Reservation;
import com.biblioteca.model.Student;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LibrarySettingsRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.ReservationRepository;
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
 * Testes de integração que simulam cenários completos de uso
 * Testa fluxos reais que envolvem múltiplos serviços
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BibliotecaIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ReservationService reservationService;

    @SuppressWarnings("unused")
    @Autowired
    private BookService bookService;

    @SuppressWarnings("unused")
    @Autowired
    private LibrarySettingsService settingsService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private LibrarySettingsRepository settingsRepository;

    private Student student1, student2, student3;
    private Book book1;
    private LibrarySettings settings;

    @BeforeEach
    public void setUp() {
        // Limpar todos os dados
        loanRepository.deleteAll();
        reservationRepository.deleteAll();
        bookRepository.deleteAll();
        studentRepository.deleteAll();
        settingsRepository.deleteAll();

        // Configurar settings padrão
        settings = new LibrarySettings();
        settings.setId(1L);
        settings.setLoanPeriodDays(14);
        settings.setMaxLoansPerStudent(3);
        settings.setFinePerDay(100);
        settingsRepository.save(settings);

        // Criar students
        student1 = createStudent("MAT001", "João Silva", "12345678901", "joao@test.com");
        student2 = createStudent("MAT002", "Maria Santos", "12345678902", "maria@test.com");
        student3 = createStudent("MAT003", "Pedro Costa", "12345678903", "pedro@test.com");

        // Criar book
        book1 = new Book();
        book1.setIsbn("978-1234567890");
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert C. Martin");
        book1.setQuantity(2);
        book1.setActiveReservationsCount(0);
        bookRepository.save(book1);
    }

    private Student createStudent(String matricula, String nome, String cpf, String email) {
        Student student = new Student();
        student.setMatricula(matricula);
        student.setNome(nome);
        student.setCpf(cpf);
        student.setDataNascimento(LocalDate.of(2000, 1, 1));
        student.setEmail(email);
        student.setReservationsCount(0);
        return studentRepository.save(student);
    }

    // ======================== CENÁRIO 1: EMPRÉSTIMO E DEVOLUÇÃO SIMPLES ========================

    @Test
    public void testCompleteSimpleLoanFlow() {
        // 1. Verificar disponibilidade do livro
        assertTrue(loanService.isBookAvailable(book1.getIsbn()));

        // 2. Criar empréstimo
        LoanRequestDTO loanRequest = new LoanRequestDTO();
        loanRequest.setBookIsbn(book1.getIsbn());
        loanRequest.setStudentMatricula(student1.getMatricula());
        LoanResponseDTO loanResponse = loanService.createLoan(loanRequest);

        assertNotNull(loanResponse);
        assertEquals(Loan.LoanStatus.ACTIVE, loanResponse.getStatus());
        assertEquals(1, loanResponse.getQuantity()); // Estoque reduzido

        // 3. Devolver no prazo
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        assertNotNull(returnedLoan);
        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
        assertEquals(0, returnedLoan.getOverdueDays());
        assertEquals(0, returnedLoan.getFineAmount());
        assertEquals(2, returnedLoan.getQuantity()); // Estoque restaurado
    }

    // ======================== CENÁRIO 2: EMPRÉSTIMO COM ATRASO E MULTA ========================

    @Test
    public void testLoanWithDelayAndFineCalculation() {
        // 1. Criar empréstimo com data no passado (20 dias atrás)
        LocalDateTime loanDate = LocalDateTime.now().minusDays(20);
        LoanRequestDTO loanRequest = new LoanRequestDTO();
        loanRequest.setBookIsbn(book1.getIsbn());
        loanRequest.setStudentMatricula(student1.getMatricula());
        loanRequest.setLoanDate(loanDate);
        LoanResponseDTO loanResponse = loanService.createLoan(loanRequest);

        // Due date: 14 dias após loanDate (6 dias atrás)
        // 2. Devolver agora (20 dias depois)
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // 3. Verificar multa (6 dias de atraso * 100 = 600 centavos)
        assertNotNull(returnedLoan);
        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
        assertTrue(returnedLoan.getOverdueDays() > 0);
        assertTrue(returnedLoan.getFineAmount() > 0);
        assertEquals(returnedLoan.getOverdueDays() * 100, returnedLoan.getFineAmount());
    }

    // ======================== CENÁRIO 3: MÚLTIPLOS EMPRÉSTIMOS DO MESMO ALUNO ========================

    @Test
    public void testMultipleLoansPerStudent() {
        // 1. Criar segundo livro
        Book book2 = new Book();
        book2.setIsbn("978-1234567891");
        book2.setTitle("Effective Java");
        book2.setAuthor("Joshua Bloch");
        book2.setQuantity(3);
        book2.setActiveReservationsCount(0);
        bookRepository.save(book2);

        // 2. Criar terceiro livro
        Book book3 = new Book();
        book3.setIsbn("978-1234567892");
        book3.setTitle("Design Patterns");
        book3.setAuthor("Gang of Four");
        book3.setQuantity(2);
        book3.setActiveReservationsCount(0);
        bookRepository.save(book3);

        // 3. Fazer 3 empréstimos
        LoanRequestDTO req1 = new LoanRequestDTO();
        req1.setBookIsbn(book1.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        LoanResponseDTO loan1 = loanService.createLoan(req1);

        LoanRequestDTO req2 = new LoanRequestDTO();
        req2.setBookIsbn(book2.getIsbn());
        req2.setStudentMatricula(student1.getMatricula());
        @SuppressWarnings("unused")
        LoanResponseDTO loan2 = loanService.createLoan(req2);

        LoanRequestDTO req3 = new LoanRequestDTO();
        req3.setBookIsbn(book3.getIsbn());
        req3.setStudentMatricula(student1.getMatricula());
        @SuppressWarnings("unused")
        LoanResponseDTO loan3 = loanService.createLoan(req3);

        // 4. Tentar 4º empréstimo (deve falhar - limite é 3)
        Book book4 = new Book();
        book4.setIsbn("978-1234567893");
        book4.setTitle("Refactoring");
        book4.setAuthor("Martin Fowler");
        book4.setQuantity(2);
        book4.setActiveReservationsCount(0);
        bookRepository.save(book4);

        assertThrows(RuntimeException.class, () -> {
            LoanRequestDTO req4 = new LoanRequestDTO();
            req4.setBookIsbn(book4.getIsbn());
            req4.setStudentMatricula(student1.getMatricula());
            loanService.createLoan(req4);
        });

        // 5. Devolver um livro
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        loanService.returnLoan(loan1.getId(), returnRequest);

        // 6. Agora pode pegar o 4º
        LoanRequestDTO req4 = new LoanRequestDTO();
        req4.setBookIsbn(book4.getIsbn());
        req4.setStudentMatricula(student1.getMatricula());
        LoanResponseDTO loan4 = loanService.createLoan(req4);

        assertNotNull(loan4);
    }

    // ======================== CENÁRIO 4: RESERVA COM FILA E REORGANIZAÇÃO ========================

    @Test
    public void testReservationQueueWithReorganization() {
        // 1. Criar reservas para todos os estudantes (3 estudantes)
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(book1.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO res1 = reservationService.createReservation(req1);
        assertEquals(1, res1.getQueuePosition());

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(book1.getIsbn());
        req2.setStudentMatricula(student2.getMatricula());
        ReservationResponseDTO res2 = reservationService.createReservation(req2);
        assertEquals(2, res2.getQueuePosition());

        ReservationRequestDTO req3 = new ReservationRequestDTO();
        req3.setBookIsbn(book1.getIsbn());
        req3.setStudentMatricula(student3.getMatricula());
        ReservationResponseDTO res3 = reservationService.createReservation(req3);
        assertEquals(3, res3.getQueuePosition());

        // 2. Verificar contador de reservas no livro
        Book bookAfterReservations = bookRepository.findById(book1.getIsbn()).orElse(null);
        assertNotNull(bookAfterReservations);
        assertEquals(3, bookAfterReservations.getActiveReservationsCount());

        // 3. Cancelar a reserva do meio
        reservationService.cancelReservation(res2.getId());

        // 4. Verificar reorganização
        List<ReservationResponseDTO> remaining = reservationService
                .getActiveReservationsByBook(book1.getIsbn());
        assertEquals(2, remaining.size());

        // res1 continua na posição 1
        Reservation res1Db = reservationRepository.findById(res1.getId()).orElse(null);
        assertNotNull(res1Db);
        assertEquals(1, res1Db.getQueuePosition());

        // res3 mudou de 3 para 2
        Reservation res3Db = reservationRepository.findById(res3.getId()).orElse(null);
        assertNotNull(res3Db);
        assertEquals(2, res3Db.getQueuePosition());

        // 5. Verificar contador atualizado
        Book bookAfterCancel = bookRepository.findById(book1.getIsbn()).orElse(null);
        assertNotNull(bookAfterCancel);
        assertEquals(2, bookAfterCancel.getActiveReservationsCount());
    }

    // ======================== CENÁRIO 5: MUDANÇA DE CONFIGURAÇÕES AFETA NOVOS EMPRÉSTIMOS ========================

    @Test
    public void testConfigurationChangesAffectNewLoans() {
        // 1. Mudar prazo de devolução para 21 dias
        settings.setLoanPeriodDays(21);
        settingsRepository.save(settings);

        // 2. Criar novo empréstimo
        LoanRequestDTO loanRequest = new LoanRequestDTO();
        loanRequest.setBookIsbn(book1.getIsbn());
        loanRequest.setStudentMatricula(student1.getMatricula());
        LoanResponseDTO loanResponse = loanService.createLoan(loanRequest);

        // 3. Verificar se a data de vencimento é 21 dias depois
        Loan loan = loanRepository.findById(loanResponse.getId()).orElse(null);
        assertNotNull(loan);
        long daysUntilDue = java.time.Duration.between(
                loan.getLoanDate(),
                loan.getDueDate()
        ).toDays();
        assertEquals(21, daysUntilDue);

        // 4. Mudar multa por dia para 150
        settings.setFinePerDay(150);
        settingsRepository.save(settings);

        // 5. Devolver com atraso
        LocalDateTime returnDate = loan.getDueDate().plusDays(5);
        LoanReturnDTO returnRequest = new LoanReturnDTO();
        returnRequest.setReturnDate(returnDate);
        LoanResponseDTO returnedLoan = loanService.returnLoan(loanResponse.getId(), returnRequest);

        // 6. Verificar se multa usa novo valor (5 dias * 150 = 750)
        assertEquals(5, returnedLoan.getOverdueDays());
        assertEquals(750, returnedLoan.getFineAmount());
    }

    // ======================== CENÁRIO 6: STATUS AUTOMÁTICO ========================

    @Test
    public void testAutomaticStatusUpdateForOverdueLoans() {
        // 1. Criar empréstimo com data no passado
        LocalDateTime loanDate = LocalDateTime.now().minusDays(20);
        LoanRequestDTO loanRequest = new LoanRequestDTO();
        loanRequest.setBookIsbn(book1.getIsbn());
        loanRequest.setStudentMatricula(student1.getMatricula());
        loanRequest.setLoanDate(loanDate);
        LoanResponseDTO loanResponse = loanService.createLoan(loanRequest);

        // 2. Listar empréstimos ativos
        List<LoanResponseDTO> activeLoans = loanService.getActiveLoans();
        // O empréstimo não deve estar em ativos (deve ter sido marcado como OVERDUE)
        assertFalse(activeLoans.stream().anyMatch(l -> l.getId().equals(loanResponse.getId())));

        // 3. Listar empréstimos em atraso
        List<LoanResponseDTO> overdueLoans = loanService.getOverdueLoans();
        // O empréstimo deve estar em atraso
        assertTrue(overdueLoans.stream().anyMatch(l -> l.getId().equals(loanResponse.getId())));
    }

    // ======================== CENÁRIO 7: LIMITE DE RESERVAS ========================

    @Test
    public void testMaximumReservationsLimit() {
        // Criar 5 alunos extras
        for (int i = 4; i <= 8; i++) {
            createStudent("MAT00" + i, "Student " + i, "1234567890" + i, "student" + i + "@test.com");
        }

        // Criar 5 reservas (máximo)
        for (int i = 1; i <= 5; i++) {
            ReservationRequestDTO request = new ReservationRequestDTO();
            request.setBookIsbn(book1.getIsbn());
            request.setStudentMatricula("MAT00" + i);
            ReservationResponseDTO response = reservationService.createReservation(request);
            assertEquals(i, response.getQueuePosition());
        }

        // 6ª reserva deve falhar
        assertThrows(RuntimeException.class, () -> {
            ReservationRequestDTO request = new ReservationRequestDTO();
            request.setBookIsbn(book1.getIsbn());
            request.setStudentMatricula("MAT006");
            reservationService.createReservation(request);
        });
    }

    // ======================== CENÁRIO 8: CONTADORES DE RESERVAS DO ESTUDANTE ========================

    @Test
    public void testStudentReservationCounters() {
        // 1. Criar segunda livro
        Book book2 = new Book();
        book2.setIsbn("978-1234567891");
        book2.setTitle("Another Book");
        book2.setAuthor("Another Author");
        book2.setQuantity(3);
        book2.setActiveReservationsCount(0);
        bookRepository.save(book2);

        // 2. Criar reservas para o aluno1 em dois livros
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(book1.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(book2.getIsbn());
        req2.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO res2 = reservationService.createReservation(req2);

        // 3. Verificar contadores
        Student updatedStudent = studentRepository.findById(student1.getMatricula()).orElse(null);
        assertNotNull(updatedStudent);
        assertEquals(2, updatedStudent.getReservationsCount());

        // 4. Cancelar uma
        reservationService.cancelReservation(res2.getId());

        // 5. Contador deve manter em 2 (conta todas, incluindo canceladas)
        updatedStudent = studentRepository.findById(student1.getMatricula()).orElse(null);
        assertNotNull(updatedStudent);
        assertEquals(2, updatedStudent.getReservationsCount());
    }
}
