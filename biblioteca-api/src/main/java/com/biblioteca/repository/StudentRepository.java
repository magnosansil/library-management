package com.biblioteca.repository;

import com.biblioteca.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
  Optional<Student> findByCpf(String cpf);

  Optional<Student> findByMatricula(String matricula);

  @Query("SELECT COUNT(l) FROM Loan l WHERE l.student.matricula = :matricula AND l.status = 'ACTIVE'")
  Long countActiveLoansByMatricula(@Param("matricula") String matricula);
}
