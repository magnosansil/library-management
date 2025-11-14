package com.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para requisição de devolução de empréstimo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanReturnDTO {
  /**
   * Data de devolução (opcional)
   * Se não informada, será usada a data/hora atual
   */
  private LocalDateTime returnDate;
}
