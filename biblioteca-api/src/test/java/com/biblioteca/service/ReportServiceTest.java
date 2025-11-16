package com.biblioteca.service;

import com.biblioteca.dto.ReportAvailabilityDTO;
import com.biblioteca.dto.ReportStudentMetricsDTO;
import com.biblioteca.dto.ReportLoanStatisticsDTO;
import com.biblioteca.dto.ReportReservationAnalyticsDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Loan;
import com.biblioteca.model.Reservation;
import com.biblioteca.model.Student;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.ReservationRepository;
import com.biblioteca.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o serviço de Relatórios
 * Cobre os 4 novos relatórios: Disponibilidade, Métricas de Alunos, 
 * Estatísticas de Empréstimos e Análise de Reservas
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @SuppressWarnings("unused")
    @Autowired
    private LoanService loanService;

    private Book book1, book2, book3;
    private Student student1, student2, student3;

    @BeforeEach
    public void setUp() {
        // Limpar dados anteriores
        loanRepository.deleteAll();
        reservationRepository.deleteAll();
        studentRepository.deleteAll();
        bookRepository.deleteAll();

        // Criar livros
        book1 = new Book();
        book1.setIsbn("978-1111111111");
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setQuantity(5);
        book1.setActiveReservationsCount(0);
        bookRepository.save(book1);

        book2 = new Book();
        book2.setIsbn("978-2222222222");
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setQuantity(0); // Sem estoque
        book2.setActiveReservationsCount(0);
        bookRepository.save(book2);

        book3 = new Book();
        book3.setIsbn("978-3333333333");
        book3.setTitle("Book 3");
        book3.setAuthor("Author 3");
        book3.setQuantity(3);
        book3.setActiveReservationsCount(2);
        bookRepository.save(book3);

        // Criar alunos
        student1 = new Student();
        student1.setMatricula("2024001");
        student1.setNome("Student 1");
        student1.setCpf("12345678901");
        student1.setDataNascimento(java.time.LocalDate.of(2000, 5, 15));
        student1.setEmail("student1@test.com");
        studentRepository.save(student1);

        student2 = new Student();
        student2.setMatricula("2024002");
        student2.setNome("Student 2");
        student2.setCpf("98765432100");
        student2.setDataNascimento(java.time.LocalDate.of(2001, 8, 20));
        student2.setEmail("student2@test.com");
        studentRepository.save(student2);

        student3 = new Student();
        student3.setMatricula("2024003");
        student3.setNome("Student 3");
        student3.setCpf("11122233344");
        student3.setDataNascimento(java.time.LocalDate.of(1999, 12, 10));
        student3.setEmail("student3@test.com");
        studentRepository.save(student3);
    }

    // ======================== TESTES - RELATÓRIO 1: DISPONIBILIDADE DO ACERVO ========================

    @Test
    public void testAvailabilityReportBasicStructure() {
        ReportAvailabilityDTO report = reportService.generateAvailabilityReport();

        assertNotNull(report);
        assertNotNull(report.getTotalBooks());
        assertNotNull(report.getAvailableBooks());
        assertNotNull(report.getUnavailableBooks());
        assertNotNull(report.getAvailabilityPercentage());
    }

    @Test
    public void testAvailabilityReportWithThreeBooks() {
        ReportAvailabilityDTO report = reportService.generateAvailabilityReport();

        assertEquals(3L, report.getTotalBooks());
        assertEquals(2L, report.getAvailableBooks()); // book1 e book3
        assertEquals(1L, report.getUnavailableBooks()); // book2
    }

    @Test
    public void testAvailabilityReportPercentageCalculation() {
        ReportAvailabilityDTO report = reportService.generateAvailabilityReport();

        // 2 de 3 livros disponíveis = 66.67%
        assertEquals(66.66666666666666, report.getAvailabilityPercentage(), 0.01);
    }

    @Test
    public void testAvailabilityReportTotalCopies() {
        ReportAvailabilityDTO report = reportService.generateAvailabilityReport();

        // Total: 5 + 0 + 3 = 8 cópias
        assertEquals(8L, report.getTotalCopiesInStock());
    }

    @Test
    public void testAvailabilityReportWithNoBooks() {
        bookRepository.deleteAll();

        ReportAvailabilityDTO report = reportService.generateAvailabilityReport();

        assertEquals(0L, report.getTotalBooks());
        assertEquals(0L, report.getAvailableBooks());
        assertEquals(0.0, report.getAvailabilityPercentage());
    }

    @Test
    public void testAvailabilityReportAvailableCopiesConsidersReservations() {
        ReportAvailabilityDTO report = reportService.generateAvailabilityReport();

        // book3 tem 3 cópias mas 2 reservas ativas
        // Total cópias = 8, Disponíveis = 5 + 0 + (3-2) = 6
        assertEquals(6L, report.getTotalCopiesAvailable());
    }

    // ======================== TESTES - RELATÓRIO 2: MÉTRICAS DE ALUNOS ========================

    @Test
    public void testStudentMetricsReportBasicStructure() {
        ReportStudentMetricsDTO report = reportService.generateStudentMetricsReport();

        assertNotNull(report);
        assertNotNull(report.getTotalStudents());
        assertNotNull(report.getStudentsWithActiveLoans());
        assertNotNull(report.getStudentsWithOverdueLoans());
    }

    @Test
    public void testStudentMetricsReportWithNoLoans() {
        ReportStudentMetricsDTO report = reportService.generateStudentMetricsReport();

        assertEquals(3L, report.getTotalStudents());
        assertEquals(0L, report.getStudentsWithActiveLoans());
        assertEquals(0L, report.getStudentsWithOverdueLoans());
        assertEquals(3L, report.getStudentsWithoutLoans());
        assertEquals(0.0, report.getAverageLoansPerStudent());
    }

    @Test
    public void testStudentMetricsReportWithActiveLoans() {
        // Criar empréstimos ativos
        Loan loan1 = new Loan();
        loan1.setStudent(student1);
        loan1.setBook(book1);
        loan1.setLoanDate(LocalDateTime.now().minusDays(5));
        loan1.setDueDate(LocalDateTime.now().plusDays(9));
        loan1.setStatus(Loan.LoanStatus.ACTIVE);
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        loan2.setStudent(student2);
        loan2.setBook(book3);
        loan2.setLoanDate(LocalDateTime.now().minusDays(3));
        loan2.setDueDate(LocalDateTime.now().plusDays(11));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanRepository.save(loan2);

        ReportStudentMetricsDTO report = reportService.generateStudentMetricsReport();

        assertEquals(3L, report.getTotalStudents());
        assertEquals(2L, report.getStudentsWithActiveLoans());
        assertEquals(1L, report.getStudentsWithoutLoans());
        assertEquals(2L, report.getTotalActiveLoans());
        assertEquals(2.0 / 3, report.getAverageLoansPerStudent(), 0.01);
    }

    @Test
    public void testStudentMetricsReportWithOverdueLoans() {
        // Criar empréstimo em atraso
        Loan loan1 = new Loan();
        loan1.setStudent(student1);
        loan1.setBook(book1);
        loan1.setLoanDate(LocalDateTime.now().minusDays(20));
        loan1.setDueDate(LocalDateTime.now().minusDays(5)); // Vencido
        loan1.setStatus(Loan.LoanStatus.OVERDUE);
        loanRepository.save(loan1);

        ReportStudentMetricsDTO report = reportService.generateStudentMetricsReport();

        assertEquals(1L, report.getStudentsWithOverdueLoans());
        assertEquals(1L, report.getTotalOverdueLoans());
    }

    // ======================== TESTES - RELATÓRIO 3: ESTATÍSTICAS DE EMPRÉSTIMOS ========================

    @Test
    public void testLoanStatisticsReportBasicStructure() {
        ReportLoanStatisticsDTO report = reportService.generateLoanStatisticsReport();

        assertNotNull(report);
        assertNotNull(report.getTotalLoans());
        assertNotNull(report.getActiveLoans());
        assertNotNull(report.getReturnedLoans());
        assertNotNull(report.getOverdueLoans());
    }

    @Test
    public void testLoanStatisticsReportWithNoLoans() {
        ReportLoanStatisticsDTO report = reportService.generateLoanStatisticsReport();

        assertEquals(0L, report.getTotalLoans());
        assertEquals(0.0, report.getActiveLoansPercentage());
        assertEquals(0L, report.getTotalFinesCollected());
    }

    @Test
    public void testLoanStatisticsReportWithMixedLoans() {
        // Criar empréstimo ativo
        Loan loan1 = new Loan();
        loan1.setStudent(student1);
        loan1.setBook(book1);
        loan1.setLoanDate(LocalDateTime.now().minusDays(5));
        loan1.setDueDate(LocalDateTime.now().plusDays(9));
        loan1.setStatus(Loan.LoanStatus.ACTIVE);
        loanRepository.save(loan1);

        // Criar empréstimo devolvido
        Loan loan2 = new Loan();
        loan2.setStudent(student2);
        loan2.setBook(book3);
        loan2.setLoanDate(LocalDateTime.now().minusDays(15));
        loan2.setDueDate(LocalDateTime.now().minusDays(1));
        loan2.setReturnDate(LocalDateTime.now());
        loan2.setStatus(Loan.LoanStatus.RETURNED);
        loan2.setOverdueDays(0);
        loan2.setFineAmount(0);
        loanRepository.save(loan2);

        ReportLoanStatisticsDTO report = reportService.generateLoanStatisticsReport();

        assertEquals(2L, report.getTotalLoans());
        assertEquals(1L, report.getActiveLoans());
        assertEquals(1L, report.getReturnedLoans());
        assertEquals(50.0, report.getActiveLoansPercentage(), 0.01);
    }

    @Test
    public void testLoanStatisticsReportFineCalculation() {
        // Criar empréstimo devolvido com multa
        Loan loan1 = new Loan();
        loan1.setStudent(student1);
        loan1.setBook(book1);
        loan1.setLoanDate(LocalDateTime.now().minusDays(20));
        loan1.setDueDate(LocalDateTime.now().minusDays(5));
        loan1.setReturnDate(LocalDateTime.now());
        loan1.setStatus(Loan.LoanStatus.RETURNED);
        loan1.setOverdueDays(5);
        loan1.setFineAmount(500); // 5 dias * 100 centavos
        loanRepository.save(loan1);

        ReportLoanStatisticsDTO report = reportService.generateLoanStatisticsReport();

        assertEquals(1L, report.getTotalLoans());
        assertEquals(500L, report.getTotalFinesCollected());
        assertEquals(500.0, report.getAverageOverdueValue(), 0.01);
    }

    @Test
    public void testLoanStatisticsReportAverageDuration() {
        // Criar empréstimo com 10 dias de duração
        Loan loan1 = new Loan();
        loan1.setStudent(student1);
        loan1.setBook(book1);
        loan1.setLoanDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        loan1.setDueDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        loan1.setReturnDate(LocalDateTime.of(2024, 1, 11, 10, 0));
        loan1.setStatus(Loan.LoanStatus.RETURNED);
        loan1.setOverdueDays(0);
        loan1.setFineAmount(0);
        loanRepository.save(loan1);

        ReportLoanStatisticsDTO report = reportService.generateLoanStatisticsReport();

        assertEquals(10.0, report.getAverageLoanDurationDays(), 0.01);
    }

    // ======================== TESTES - RELATÓRIO 4: ANÁLISE DE RESERVAS ========================

    @Test
    public void testReservationAnalyticsReportBasicStructure() {
        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertNotNull(report);
        assertNotNull(report.getTotalReservations());
        assertNotNull(report.getActiveReservations());
        assertNotNull(report.getFulfilledReservations());
        assertNotNull(report.getCancelledReservations());
    }

    @Test
    public void testReservationAnalyticsReportWithNoReservations() {
        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertEquals(0L, report.getTotalReservations());
        assertEquals(0L, report.getActiveReservations());
        assertEquals(0.0, report.getFulfillmentRate());
    }

    @Test
    public void testReservationAnalyticsReportWithActiveReservations() {
        // Criar 2 reservas ativas para book1
        Reservation res1 = new Reservation();
        res1.setBook(book1);
        res1.setStudent(student1);
        res1.setReservationDate(LocalDateTime.now());
        res1.setQueuePosition(1);
        res1.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepository.save(res1);

        Reservation res2 = new Reservation();
        res2.setBook(book1);
        res2.setStudent(student2);
        res2.setReservationDate(LocalDateTime.now());
        res2.setQueuePosition(2);
        res2.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepository.save(res2);

        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertEquals(2L, report.getTotalReservations());
        assertEquals(2L, report.getActiveReservations());
        assertEquals(1L, report.getBooksWithReservations());
    }

    @Test
    public void testReservationAnalyticsReportWithFulfilledReservations() {
        // Criar reserva efetivada
        Reservation res1 = new Reservation();
        res1.setBook(book1);
        res1.setStudent(student1);
        res1.setReservationDate(LocalDateTime.now().minusDays(5));
        res1.setQueuePosition(1);
        res1.setStatus(Reservation.ReservationStatus.FULFILLED);
        reservationRepository.save(res1);

        // Criar reserva ativa
        Reservation res2 = new Reservation();
        res2.setBook(book1);
        res2.setStudent(student2);
        res2.setReservationDate(LocalDateTime.now());
        res2.setQueuePosition(2);
        res2.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepository.save(res2);

        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertEquals(2L, report.getTotalReservations());
        assertEquals(1L, report.getFulfilledReservations());
        assertEquals(1L, report.getActiveReservations());
        assertEquals(50.0, report.getFulfillmentRate(), 0.01);
    }

    @Test
    public void testReservationAnalyticsReportCancelledReservations() {
        // Criar reserva cancelada
        Reservation res1 = new Reservation();
        res1.setBook(book1);
        res1.setStudent(student1);
        res1.setReservationDate(LocalDateTime.now());
        res1.setQueuePosition(1);
        res1.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(res1);

        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertEquals(1L, report.getTotalReservations());
        assertEquals(1L, report.getCancelledReservations());
        assertEquals(0.0, report.getFulfillmentRate());
    }

    @Test
    public void testReservationAnalyticsReportBooksWithFullQueue() {
        // book3 já tem 2 reservas ativas no setUp, adicionar mais para completar 5
        for (int i = 1; i <= 3; i++) {
            Student temp = new Student();
            temp.setMatricula("temp" + i);
            temp.setNome("Temp " + i);
            temp.setCpf("0000000000" + i);
            temp.setDataNascimento(java.time.LocalDate.now());
            temp.setEmail("temp" + i + "@test.com");
            studentRepository.save(temp);

            Reservation res = new Reservation();
            res.setBook(book3);
            res.setStudent(temp);
            res.setReservationDate(LocalDateTime.now());
            res.setQueuePosition(3 + i - 1);
            res.setStatus(Reservation.ReservationStatus.ACTIVE);
            reservationRepository.save(res);
        }

        // Atualizar contador de reservas do book3
        book3.setActiveReservationsCount(5);
        bookRepository.save(book3);

        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertEquals(1L, report.getBooksWithFullQueue());
    }

    @Test
    public void testReservationAnalyticsReportMultipleStudentsWithReservations() {
        // Criar reservas para múltiplos alunos
        Reservation res1 = new Reservation();
        res1.setBook(book1);
        res1.setStudent(student1);
        res1.setReservationDate(LocalDateTime.now());
        res1.setQueuePosition(1);
        res1.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepository.save(res1);

        Reservation res2 = new Reservation();
        res2.setBook(book3);
        res2.setStudent(student2);
        res2.setReservationDate(LocalDateTime.now());
        res2.setQueuePosition(3);
        res2.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepository.save(res2);

        ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();

        assertEquals(2L, report.getStudentsWithReservations());
    }
}
