package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de notificação de livro em atraso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverdueNotificationDTO {
  @NotNull(message = "ID do empréstimo é obrigatório")
  private Long loanId;
}
