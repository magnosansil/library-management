package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de notificação de livro em atraso
 */
public class OverdueNotificationDTO {
  @NotNull(message = "ID do empréstimo é obrigatório")
  private Long loanId;

  // Constructors
  public OverdueNotificationDTO() {
  }

  public OverdueNotificationDTO(Long loanId) {
    this.loanId = loanId;
  }

  // Getters and Setters
  public Long getLoanId() {
    return loanId;
  }

  public void setLoanId(Long loanId) {
    this.loanId = loanId;
  }
}
