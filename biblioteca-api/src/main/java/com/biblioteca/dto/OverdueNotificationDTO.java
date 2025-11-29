package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO destinado a notificações de empréstimos atrasados.
 * Reúne informações sobre o empréstimo, o usuário, a data prevista de devolução
 * e o número de dias de atraso, permitindo a geração de alertas e avisos.
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
