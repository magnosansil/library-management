package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo de Reserva de Livro
 * Implementa uma fila de reservas com máximo de 5 posições por livro
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_isbn", nullable = false)
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_matricula", nullable = false)
  private Student student;

  /**
   * Data e hora da reserva
   */
  @Column(name = "reservation_date", nullable = false)
  private LocalDateTime reservationDate;

  /**
   * Posição na fila de reservas (1 a 5)
   * Usado para ordenar as reservas do mesmo livro
   */
  @Column(name = "queue_position", nullable = false)
  private Integer queuePosition;

  /**
   * Status da reserva
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status = ReservationStatus.ACTIVE;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (reservationDate == null) {
      reservationDate = LocalDateTime.now();
    }
  }

  public enum ReservationStatus {
    ACTIVE, // Reserva ativa na fila
    CANCELLED, // Reserva cancelada
    FULFILLED // Reserva efetivada (gerou empréstimo)
  }
}
