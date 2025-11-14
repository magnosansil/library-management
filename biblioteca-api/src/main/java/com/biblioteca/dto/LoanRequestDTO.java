package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
  @NotNull(message = "Matrícula do aluno é obrigatória")
  private String studentMatricula;

  @NotNull(message = "ISBN do livro é obrigatório")
  private String bookIsbn;
}
