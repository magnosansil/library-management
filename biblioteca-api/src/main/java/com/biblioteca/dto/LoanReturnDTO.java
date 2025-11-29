package com.biblioteca.dto;

import java.time.LocalDateTime;

/**
 * DTO para requisição de devolução de empréstimo
 */
/**
 * DTO usado para requisições de devolução de empréstimos.
 * Contém as informações necessárias para registrar a devolução,
 * como id do empréstimo, data de devolução efetiva e observações.
 */

public class LoanReturnDTO {
  /**
   * Data de devolução (opcional)
   * Se não informada, será usada a data/hora atual
   */
  private LocalDateTime returnDate;

  // Constructors
  public LoanReturnDTO() {
  }

  public LoanReturnDTO(LocalDateTime returnDate) {
    this.returnDate = returnDate;
  }

  // Getters and Setters
  public LocalDateTime getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDateTime returnDate) {
    this.returnDate = returnDate;
  }
}
