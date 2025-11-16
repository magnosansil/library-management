package com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * DTO para requisição de criação de reserva
 */
public class ReservationRequestDTO {
  @NotBlank(message = "ISBN do livro é obrigatório")
  private String bookIsbn;

  @NotBlank(message = "Matrícula do estudante é obrigatória")
  private String studentMatricula;

  /**
   * Data de reserva (opcional)
   * Se não informada, será usada a data/hora atual
   */
  private LocalDateTime reservationDate;

  // Constructors
  public ReservationRequestDTO() {
  }

  public ReservationRequestDTO(String bookIsbn, String studentMatricula, LocalDateTime reservationDate) {
    this.bookIsbn = bookIsbn;
    this.studentMatricula = studentMatricula;
    this.reservationDate = reservationDate;
  }

  // Getters and Setters
  public String getBookIsbn() {
    return bookIsbn;
  }

  public void setBookIsbn(String bookIsbn) {
    this.bookIsbn = bookIsbn;
  }

  public String getStudentMatricula() {
    return studentMatricula;
  }

  public void setStudentMatricula(String studentMatricula) {
    this.studentMatricula = studentMatricula;
  }

  public LocalDateTime getReservationDate() {
    return reservationDate;
  }

  public void setReservationDate(LocalDateTime reservationDate) {
    this.reservationDate = reservationDate;
  }
}
