package com.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

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

  // Constructors
  public LoanRequestDTO() {
  }

  public LoanRequestDTO(String studentMatricula, String bookIsbn, LocalDateTime loanDate) {
    this.studentMatricula = studentMatricula;
    this.bookIsbn = bookIsbn;
    this.loanDate = loanDate;
  }

  // Getters and Setters
  public String getStudentMatricula() {
    return studentMatricula;
  }

  public void setStudentMatricula(String studentMatricula) {
    this.studentMatricula = studentMatricula;
  }

  public String getBookIsbn() {
    return bookIsbn;
  }

  public void setBookIsbn(String bookIsbn) {
    this.bookIsbn = bookIsbn;
  }

  public LocalDateTime getLoanDate() {
    return loanDate;
  }

  public void setLoanDate(LocalDateTime loanDate) {
    this.loanDate = loanDate;
  }
}
