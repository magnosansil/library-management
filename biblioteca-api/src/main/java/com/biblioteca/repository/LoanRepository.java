package com.biblioteca.repository;

import com.biblioteca.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStudentMatriculaAndStatus(String matricula, Loan.LoanStatus status);

    List<Loan> findByStatus(Loan.LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :currentDateTime")
    List<Loan> findOverdueLoans(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT l FROM Loan l WHERE l.student.matricula = :matricula AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByMatricula(@Param("matricula") String matricula);

    /**
     * Conta todos os empréstimos de um livro (em qualquer status)
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.book.isbn = :isbn")
    Long countLoansByBookIsbn(@Param("isbn") String isbn);
}
