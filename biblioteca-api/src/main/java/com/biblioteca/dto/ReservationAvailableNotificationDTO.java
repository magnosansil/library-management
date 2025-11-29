package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para notificar que um item reservado está disponível.
 * Inclui informações sobre o usuário, o item, a data de disponibilidade
 * e o prazo para retirada do material.
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
