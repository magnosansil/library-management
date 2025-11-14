package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
  @NotNull(message = "Matrícula do aluno é obrigatória")
  private String studentMatricula;

  @NotNull(message = "ISBN do livro é obrigatório")
  private String bookIsbn;

  /**
   * Data de empréstimo (opcional)
   * Se não informada, será usada a data/hora atual
   */
  private LocalDateTime loanDate;
}
