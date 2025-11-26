package com.biblioteca.repository;

import com.biblioteca.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Student.
 *
 * <p>Fornece consultas para busca por CPF, matrícula, e-mail (case-insensitive)
 * e contagem de empréstimos ativos do estudante.</p>
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    /**
     * Busca estudante por CPF.
     *
     * @param cpf CPF do estudante
     * @return Optional contendo o estudante, se encontrado
     */
    Optional<Student> findByCpf(String cpf);

    /**
     * Busca estudante por matrícula.
     *
     * @param matricula matrícula do estudante
     * @return Optional contendo o estudante, se encontrado
     */
    Optional<Student> findByMatricula(String matricula);

    /**
     * Busca estudante por e-mail (ignorando maiúsculas/minúsculas).
     *
     * @param email e-mail do estudante
     * @return Optional contendo o estudante, se encontrado
     */
    @Query("SELECT s FROM Student s WHERE LOWER(s.email) = LOWER(:email)")
    Optional<Student> findByEmail(@Param("email") String email);

    /**
     * Conta empréstimos ativos (ACTIVE ou OVERDUE) de um estudante pela matrícula.
     *
     * @param matricula matrícula do estudante
     * @return número de empréstimos ativos/atrasados do estudante
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.student.matricula = :matricula AND (l.status = 'ACTIVE' OR l.status = 'OVERDUE')")
    Long countActiveLoansByMatricula(@Param("matricula") String matricula);
}
