package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de notificação de livro reservado disponível
 */
public class ReservationAvailableNotificationDTO {
  @NotNull(message = "ID da reserva é obrigatório")
  private Long reservationId;

  // Constructors
  public ReservationAvailableNotificationDTO() {
  }

  public ReservationAvailableNotificationDTO(Long reservationId) {
    this.reservationId = reservationId;
  }

  // Getters and Setters
  public Long getReservationId() {
    return reservationId;
  }

  public void setReservationId(Long reservationId) {
    this.reservationId = reservationId;
  }
}
