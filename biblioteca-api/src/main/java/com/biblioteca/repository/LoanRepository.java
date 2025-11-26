package com.biblioteca.repository;

import com.biblioteca.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para a entidade Loan.
 *
 * <p>Contém consultas específicas para listar empréstimos por aluno, status,
 * empréstimos vencidos e contagem de empréstimos por ISBN.</p>
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    /**
     * Busca empréstimos de um estudante pelo número de matrícula e status.
     *
     * @param matricula número de matrícula do estudante
     * @param status status do empréstimo
     * @return lista de empréstimos que correspondem aos critérios
     */
    List<Loan> findByStudentMatriculaAndStatus(String matricula, Loan.LoanStatus status);

    /**
     * Busca empréstimos por status.
     *
     * @param status status do empréstimo
     * @return lista de empréstimos com o status fornecido
     */
    List<Loan> findByStatus(Loan.LoanStatus status);

    /**
     * Busca empréstimos ativos cujo prazo de devolução já passou.
     *
     * @param currentDateTime data/hora atual para comparação com dueDate
     * @return lista de empréstimos em atraso
     */
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :currentDateTime")
    List<Loan> findOverdueLoans(@Param("currentDateTime") LocalDateTime currentDateTime);

    /**
     * Busca empréstimos ativos de um estudante pela matrícula.
     *
     * @param matricula número de matrícula do estudante
     * @return lista de empréstimos ativos do estudante
     */
    @Query("SELECT l FROM Loan l WHERE l.student.matricula = :matricula AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByMatricula(@Param("matricula") String matricula);

    /**
     * Conta todos os empréstimos relacionados a um determinado livro,
     * independente do status do empréstimo.
     *
     * @param isbn ISBN do livro
     * @return quantidade total de empréstimos do livro
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.book.isbn = :isbn")
    Long countLoansByBookIsbn(@Param("isbn") String isbn);
}
