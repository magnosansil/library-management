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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serviço de Relatórios
 * Fornece 4 novos relatórios úteis para análise do sistema:
 * 1. Relatório de Disponibilidade do Acervo
 * 2. Relatório de Métricas de Alunos
 * 3. Relatório de Estatísticas de Empréstimos
 * 4. Relatório de Análise de Reservas
 */
@Service
public class ReportService {

    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final LoanService loanService;

    @Autowired
    public ReportService(BookRepository bookRepository,
                         StudentRepository studentRepository,
                         LoanRepository loanRepository,
                         ReservationRepository reservationRepository,
                         LoanService loanService) {
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.loanService = loanService;
    }

    /**
     * RELATÓRIO 1: Disponibilidade do Acervo
     * 
     * Retorna:
     * - Total de títulos únicos
     * - Quantidade de títulos com pelo menos 1 cópia disponível
     * - Quantidade de títulos sem cópias disponíveis
     * - Percentual de disponibilidade
     * - Total de cópias em estoque
     * - Total de cópias disponíveis
     */
    @Transactional(readOnly = true)
    public ReportAvailabilityDTO generateAvailabilityReport() {
        List<Book> allBooks = bookRepository.findAll();

        if (allBooks.isEmpty()) {
            return new ReportAvailabilityDTO(0L, 0L, 0L, 0.0, 0L, 0L);
        }

        long totalBooks = allBooks.size();
        long availableBooks = 0;
        long totalCopiesInStock = 0;
        long totalCopiesAvailable = 0;

        for (Book book : allBooks) {
            totalCopiesInStock += book.getQuantity();
            totalCopiesAvailable += Math.max(0, book.getQuantity() - book.getActiveReservationsCount());

            if (book.getQuantity() > 0) {
                availableBooks++;
            }
        }

        long unavailableBooks = totalBooks - availableBooks;
        double availabilityPercentage = totalBooks > 0 ? 
            (double) availableBooks / totalBooks * 100 : 0.0;

        return new ReportAvailabilityDTO(
            totalBooks,
            availableBooks,
            unavailableBooks,
            availabilityPercentage,
            totalCopiesInStock,
            totalCopiesAvailable
        );
    }

    /**
     * RELATÓRIO 2: Métricas de Alunos
     * 
     * Retorna:
     * - Total de alunos cadastrados
     * - Alunos com empréstimos ativos
     * - Alunos com empréstimos em atraso
     * - Alunos sem empréstimos
     * - Média de empréstimos por aluno
     * - Média de dias de atraso por aluno
     * - Total de empréstimos ativos
     * - Total de empréstimos em atraso
     */
    @Transactional(readOnly = true)
    public ReportStudentMetricsDTO generateStudentMetricsReport() {
        List<Student> allStudents = studentRepository.findAll();
        List<Loan> allLoans = loanRepository.findAll();

        // Atualizar status dos empréstimos
        loanService.updateLoansStatus(allLoans);
        allLoans = loanRepository.findAll(); // Recarregar após atualização

        long totalStudents = allStudents.size();
        if (totalStudents == 0) {
            return new ReportStudentMetricsDTO(0L, 0L, 0L, 0L, 0.0, 0.0, 0L, 0L);
        }

        Set<String> studentsWithActiveLoans = new HashSet<>();
        Set<String> studentsWithOverdueLoans = new HashSet<>();
        long totalActiveLoans = 0;
        long totalOverdueLoans = 0;
        long totalOverdueAggregated = 0;

        for (Loan loan : allLoans) {
            String studentMatricula = loan.getStudent().getMatricula();

            if (loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                studentsWithActiveLoans.add(studentMatricula);
                totalActiveLoans++;
            } else if (loan.getStatus() == Loan.LoanStatus.OVERDUE) {
                studentsWithOverdueLoans.add(studentMatricula);
                totalOverdueLoans++;
            } else if (loan.getStatus() == Loan.LoanStatus.RETURNED && 
                       loan.getOverdueDays() != null && loan.getOverdueDays() > 0) {
                totalOverdueAggregated += loan.getOverdueDays();
            }
        }

        long studentsWithoutLoans = totalStudents - studentsWithActiveLoans.size() - studentsWithOverdueLoans.size();
        double averageLoansPerStudent = allLoans.isEmpty() ? 0.0 : 
            (double) allLoans.size() / totalStudents;
        double averageOverdueDaysPerStudent = totalStudents > 0 ? 
            (double) totalOverdueAggregated / totalStudents : 0.0;

        return new ReportStudentMetricsDTO(
            totalStudents,
            (long) studentsWithActiveLoans.size(),
            (long) studentsWithOverdueLoans.size(),
            studentsWithoutLoans,
            averageLoansPerStudent,
            averageOverdueDaysPerStudent,
            totalActiveLoans,
            totalOverdueLoans
        );
    }

    /**
     * RELATÓRIO 3: Estatísticas de Empréstimos
     * 
     * Retorna:
     * - Total de empréstimos
     * - Empréstimos ativos
     * - Empréstimos devolvidos
     * - Empréstimos em atraso
     * - Percentuais de cada status
     * - Valor médio de multa (em centavos)
     * - Total de multas coletadas
     * - Duração média dos empréstimos em dias
     */
    @Transactional(readOnly = true)
    public ReportLoanStatisticsDTO generateLoanStatisticsReport() {
        List<Loan> allLoans = loanRepository.findAll();

        // Atualizar status dos empréstimos
        loanService.updateLoansStatus(allLoans);
        allLoans = loanRepository.findAll(); // Recarregar após atualização

        if (allLoans.isEmpty()) {
            return new ReportLoanStatisticsDTO(0L, 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, 0L, 0.0);
        }

        long totalLoans = allLoans.size();
        long activeLoans = 0;
        long returnedLoans = 0;
        long overdueLoans = 0;
        long totalFinesCollected = 0;
        long totalDurationDays = 0;
        int countReturnedLoans = 0;

        for (Loan loan : allLoans) {
            if (loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                activeLoans++;
            } else if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
                returnedLoans++;
                if (loan.getFineAmount() != null) {
                    totalFinesCollected += loan.getFineAmount();
                }
                // Calcular duração do empréstimo
                long durationDays = ChronoUnit.DAYS.between(
                    loan.getLoanDate().toLocalDate(),
                    loan.getReturnDate().toLocalDate()
                );
                totalDurationDays += durationDays;
                countReturnedLoans++;
            } else if (loan.getStatus() == Loan.LoanStatus.OVERDUE) {
                overdueLoans++;
            }
        }

        double activePercentage = (double) activeLoans / totalLoans * 100;
        double returnedPercentage = (double) returnedLoans / totalLoans * 100;
        double overduePercentage = (double) overdueLoans / totalLoans * 100;
        double averageFineAmount = totalFinesCollected > 0 && returnedLoans > 0 ? 
            (double) totalFinesCollected / returnedLoans : 0.0;
        double averageLoanDuration = countReturnedLoans > 0 ? 
            (double) totalDurationDays / countReturnedLoans : 0.0;

        return new ReportLoanStatisticsDTO(
            totalLoans,
            activeLoans,
            returnedLoans,
            overdueLoans,
            activePercentage,
            returnedPercentage,
            overduePercentage,
            averageFineAmount,
            totalFinesCollected,
            averageLoanDuration
        );
    }

    /**
     * RELATÓRIO 4: Análise de Reservas
     * 
     * Retorna:
     * - Total de reservas registradas
     * - Reservas ativas
     * - Reservas efetivadas
     * - Reservas canceladas
     * - Taxa de efetivação (efetivadas / total)
     * - Posição média na fila
     * - Total de livros com reservas
     * - Total de livros com fila cheia (5 posições)
     * - Tempo médio de espera em dias
     * - Total de alunos com reservas
     */
    @Transactional(readOnly = true)
    public ReportReservationAnalyticsDTO generateReservationAnalyticsReport() {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<Book> allBooks = bookRepository.findAll();

        if (allReservations.isEmpty()) {
            return new ReportReservationAnalyticsDTO(0L, 0L, 0L, 0L, 0.0, 0.0, 0L, 0L, 0.0, 0L);
        }

        long totalReservations = allReservations.size();
        long activeReservations = 0;
        long fulfilledReservations = 0;
        long cancelledReservations = 0;
        long totalWaitTimeInDays = 0;
        int countFulfilledReservations = 0;
        Set<String> booksWithReservations = new HashSet<>();
        Set<String> studentsWithReservations = new HashSet<>();

        for (Reservation reservation : allReservations) {
            booksWithReservations.add(reservation.getBook().getIsbn());
            studentsWithReservations.add(reservation.getStudent().getMatricula());

            if (reservation.getStatus() == Reservation.ReservationStatus.ACTIVE) {
                activeReservations++;
            } else if (reservation.getStatus() == Reservation.ReservationStatus.FULFILLED) {
                fulfilledReservations++;
                countFulfilledReservations++;
            } else if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
                cancelledReservations++;
            }
        }

        long booksWithFullQueue = 0;
        double averageQueuePosition = 0.0;
        long totalQueuePositions = 0;

        for (Book book : allBooks) {
            if (book.getActiveReservationsCount() >= 5) {
                booksWithFullQueue++;
            }
            totalQueuePositions += book.getActiveReservationsCount();
        }

        averageQueuePosition = allBooks.isEmpty() ? 0.0 : 
            (double) totalQueuePositions / allBooks.size();
        double fulfillmentRate = (double) fulfilledReservations / totalReservations * 100;
        double averageWaitTime = countFulfilledReservations > 0 ? 
            (double) totalWaitTimeInDays / countFulfilledReservations : 0.0;

        return new ReportReservationAnalyticsDTO(
            totalReservations,
            activeReservations,
            fulfilledReservations,
            cancelledReservations,
            fulfillmentRate,
            averageQueuePosition,
            (long) booksWithReservations.size(),
            booksWithFullQueue,
            averageWaitTime,
            (long) studentsWithReservations.size()
        );
    }
}
