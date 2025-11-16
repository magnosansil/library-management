package com.biblioteca.dto;

import com.biblioteca.model.Reservation;

import java.time.LocalDateTime;

/**
 * DTO para resposta de reserva
 */
public class ReservationResponseDTO {
  private Long id;
  private String bookIsbn;
  private String bookTitle;
  private String bookAuthor;
  private String studentMatricula;
  private String studentName;
  private LocalDateTime reservationDate;
  private Integer queuePosition;
  private Reservation.ReservationStatus status;
  private LocalDateTime createdAt;

  // Constructors
  public ReservationResponseDTO() {
  }

  public ReservationResponseDTO(Long id, String bookIsbn, String bookTitle, String bookAuthor,
                               String studentMatricula, String studentName, LocalDateTime reservationDate,
                               Integer queuePosition, Reservation.ReservationStatus status, LocalDateTime createdAt) {
    this.id = id;
    this.bookIsbn = bookIsbn;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.studentMatricula = studentMatricula;
    this.studentName = studentName;
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

  public String getBookIsbn() {
    return bookIsbn;
  }

  public void setBookIsbn(String bookIsbn) {
    this.bookIsbn = bookIsbn;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }

  public String getBookAuthor() {
    return bookAuthor;
  }

  public void setBookAuthor(String bookAuthor) {
    this.bookAuthor = bookAuthor;
  }

  public String getStudentMatricula() {
    return studentMatricula;
  }

  public void setStudentMatricula(String studentMatricula) {
    this.studentMatricula = studentMatricula;
  }

  public String getStudentName() {
    return studentName;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
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

  public Reservation.ReservationStatus getStatus() {
    return status;
  }

  public void setStatus(Reservation.ReservationStatus status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public static ReservationResponseDTO fromEntity(Reservation reservation) {
    ReservationResponseDTO dto = new ReservationResponseDTO();
    dto.setId(reservation.getId());
    dto.setBookIsbn(reservation.getBook().getIsbn());
    dto.setBookTitle(reservation.getBook().getTitle());
    dto.setBookAuthor(reservation.getBook().getAuthor());
    dto.setStudentMatricula(reservation.getStudent().getMatricula());
    dto.setStudentName(reservation.getStudent().getNome());
    dto.setReservationDate(reservation.getReservationDate());
    dto.setQueuePosition(reservation.getQueuePosition());
    dto.setStatus(reservation.getStatus());
    dto.setCreatedAt(reservation.getCreatedAt());
    return dto;
  }
}
