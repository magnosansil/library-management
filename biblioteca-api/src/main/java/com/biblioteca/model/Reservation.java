package com.biblioteca.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Modelo de Reserva de Livro
 * Implementa uma fila de reservas com máximo de 5 posições por livro
 */
@Entity
@Table(name = "reservations")
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

  // Constructors
  public Reservation() {
  }

  public Reservation(Book book, Student student, LocalDateTime reservationDate, Integer queuePosition,
                     ReservationStatus status, LocalDateTime createdAt) {
    this.book = book;
    this.student = student;
    this.reservationDate = reservationDate;
    this.queuePosition = queuePosition;
    this.status = status;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public LocalDateTime getReservationDate() {
    return reservationDate;
  }

  public void setReservationDate(LocalDateTime reservationDate) {
    this.reservationDate = reservationDate;
  }

  public Integer getQueuePosition() {
    return queuePosition;
  }

  public void setQueuePosition(Integer queuePosition) {
    this.queuePosition = queuePosition;
  }

  public ReservationStatus getStatus() {
    return status;
  }

  public void setStatus(ReservationStatus status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

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
