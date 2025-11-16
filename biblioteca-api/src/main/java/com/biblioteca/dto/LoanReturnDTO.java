package com.biblioteca.dto;

import java.time.LocalDateTime;

/**
 * DTO para requisição de devolução de empréstimo
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
