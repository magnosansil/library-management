package com.biblioteca.repository;

import com.biblioteca.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  /**
   * Busca todas as reservas ativas de um livro, ordenadas por posição na fila
   */
  @Query("SELECT r FROM Reservation r WHERE r.book.isbn = :isbn AND r.status = 'ACTIVE' ORDER BY r.queuePosition ASC")
  List<Reservation> findActiveReservationsByBookIsbnOrderByPosition(@Param("isbn") String isbn);

  /**
   * Conta reservas ativas de um livro
   */
  @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.isbn = :isbn AND r.status = 'ACTIVE'")
  Long countActiveReservationsByBookIsbn(@Param("isbn") String isbn);

  /**
   * Busca reservas ativas de um estudante
   */
  @Query("SELECT r FROM Reservation r WHERE r.student.matricula = :matricula AND r.status = 'ACTIVE' ORDER BY r.reservationDate ASC")
  List<Reservation> findActiveReservationsByStudentMatricula(@Param("matricula") String matricula);

  /**
   * Busca a maior posição na fila de um livro
   */
  @Query("SELECT COALESCE(MAX(r.queuePosition), 0) FROM Reservation r WHERE r.book.isbn = :isbn AND r.status = 'ACTIVE'")
  Integer findMaxQueuePositionByBookIsbn(@Param("isbn") String isbn);

  /**
   * Verifica se estudante já tem reserva ativa para o livro
   */
  @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.student.matricula = :matricula AND r.book.isbn = :isbn AND r.status = 'ACTIVE'")
  boolean existsActiveReservationByStudentAndBook(@Param("matricula") String matricula, @Param("isbn") String isbn);
}
