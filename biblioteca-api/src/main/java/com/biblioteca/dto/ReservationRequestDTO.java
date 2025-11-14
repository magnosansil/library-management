package com.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para requisição de criação de reserva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
