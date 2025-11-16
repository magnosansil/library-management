package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Configurações globais da biblioteca
 * Sempre haverá apenas uma instância desta entidade no banco (singleton)
 */
@Entity
@Table(name = "library_settings")
public class LibrarySettings {

  @Id
  private Long id = 1L; // Sempre ID 1 para garantir singleton

  /**
   * Prazo padrão de devolução em dias
   */
  @NotNull(message = "Prazo de devolução é obrigatório")
  @Min(value = 1, message = "Prazo de devolução deve ser pelo menos 1 dia")
  @Column(name = "loan_period_days", nullable = false)
  private Integer loanPeriodDays = 14; // Padrão: 14 dias

  /**
   * Limite máximo de empréstimos simultâneos por aluno
   */
  @NotNull(message = "Limite de empréstimos é obrigatório")
  @Min(value = 1, message = "Limite de empréstimos deve ser pelo menos 1")
  @Column(name = "max_loans_per_student", nullable = false)
  private Integer maxLoansPerStudent = 3; // Padrão: 3 empréstimos

  /**
   * Multa por dia de atraso (em centavos ou unidade mínima de moeda)
   * A formatação para dinheiro será feita no front-end
   */
  @NotNull(message = "Multa por dia é obrigatória")
  @Min(value = 0, message = "Multa por dia não pode ser negativa")
  @Column(name = "fine_per_day", nullable = false)
  private Integer finePerDay = 100; // Padrão: 100 (centavos ou unidade mínima)

  // Constructors
  public LibrarySettings() {
  }

  public LibrarySettings(Integer loanPeriodDays, Integer maxLoansPerStudent, Integer finePerDay) {
    this.loanPeriodDays = loanPeriodDays;
    this.maxLoansPerStudent = maxLoansPerStudent;
    this.finePerDay = finePerDay;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getLoanPeriodDays() {
    return loanPeriodDays;
  }

  public void setLoanPeriodDays(Integer loanPeriodDays) {
    this.loanPeriodDays = loanPeriodDays;
  }

  public Integer getMaxLoansPerStudent() {
    return maxLoansPerStudent;
  }

  public void setMaxLoansPerStudent(Integer maxLoansPerStudent) {
    this.maxLoansPerStudent = maxLoansPerStudent;
  }

  public Integer getFinePerDay() {
    return finePerDay;
  }

  public void setFinePerDay(Integer finePerDay) {
    this.finePerDay = finePerDay;
  }

  /**
   * Garantir que sempre existe apenas uma instância
   */
  @PrePersist
  protected void onCreate() {
    this.id = 1L;
  }
}
