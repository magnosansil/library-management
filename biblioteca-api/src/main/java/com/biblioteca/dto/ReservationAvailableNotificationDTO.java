package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de notificação de livro reservado disponível
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAvailableNotificationDTO {
  @NotNull(message = "ID da reserva é obrigatório")
  private Long reservationId;
}
