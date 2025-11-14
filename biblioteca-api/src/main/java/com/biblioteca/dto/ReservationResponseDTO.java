package com.biblioteca.dto;

import com.biblioteca.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de reserva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
