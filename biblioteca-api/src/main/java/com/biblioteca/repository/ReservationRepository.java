package com.biblioteca.repository;

import com.biblioteca.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade Reservation.
 *
 * <p>Contém consultas para manipular reservas ativas, contagem e verificação
 * de existência na fila de reservas.</p>
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Busca todas as reservas ativas de um livro, ordenadas por posição na fila.
     *
     * @param isbn ISBN do livro
     * @return lista de reservas ativas ordenadas pela posição na fila (asc)
     */
    @Query("SELECT r FROM Reservation r WHERE r.book.isbn = :isbn AND r.status = 'ACTIVE' ORDER BY r.queuePosition ASC")
    List<Reservation> findActiveReservationsByBookIsbnOrderByPosition(@Param("isbn") String isbn);

    /**
     * Conta quantas reservas ativas existem para um livro.
     *
     * @param isbn ISBN do livro
     * @return número de reservas ativas do livro
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.isbn = :isbn AND r.status = 'ACTIVE'")
    Long countActiveReservationsByBookIsbn(@Param("isbn") String isbn);

    /**
     * Busca reservas ativas de um estudante, ordenadas pela data da reserva.
     *
     * @param matricula matrícula do estudante
     * @return lista de reservas ativas do estudante ordenadas por data
     */
    @Query("SELECT r FROM Reservation r WHERE r.student.matricula = :matricula AND r.status = 'ACTIVE' ORDER BY r.reservationDate ASC")
    List<Reservation> findActiveReservationsByStudentMatricula(@Param("matricula") String matricula);

    /**
     * Encontra a maior posição na fila de reservas para um livro.
     *
     * @param isbn ISBN do livro
     * @return maior posição na fila (0 se não houver reservas ativas)
     */
    @Query("SELECT COALESCE(MAX(r.queuePosition), 0) FROM Reservation r WHERE r.book.isbn = :isbn AND r.status = 'ACTIVE'")
    Integer findMaxQueuePositionByBookIsbn(@Param("isbn") String isbn);

    /**
     * Verifica se um estudante já possui uma reserva ativa para um livro.
     *
     * @param matricula matrícula do estudante
     * @param isbn ISBN do livro
     * @return true se existir uma reserva ativa do estudante para o livro
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.student.matricula = :matricula AND r.book.isbn = :isbn AND r.status = 'ACTIVE'")
    boolean existsActiveReservationByStudentAndBook(@Param("matricula") String matricula, @Param("isbn") String isbn);

    /**
     * Conta todas as reservas de um livro, independentemente do status.
     *
     * @param isbn ISBN do livro
     * @return número total de reservas do livro
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.isbn = :isbn")
    Long countReservationsByBookIsbn(@Param("isbn") String isbn);
}
