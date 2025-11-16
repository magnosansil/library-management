package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_matricula", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_isbn", nullable = false)
  private Book book;

  @Column(name = "loan_date", nullable = false)
  private LocalDateTime loanDate;

  @Column(name = "due_date", nullable = false)
  private LocalDateTime dueDate;

  @Column(name = "return_date")
  private LocalDateTime returnDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoanStatus status = LoanStatus.ACTIVE;

  /**
   * Dias de atraso (calculado na devolução)
   * Null se não houver atraso ou se ainda não foi devolvido
   */
  @Column(name = "overdue_days")
  private Integer overdueDays;

  /**
   * Valor da multa (em centavos ou unidade mínima de moeda)
   * Calculado como: overdueDays * finePerDay (das configurações)
   * Null se não houver multa ou se ainda não foi devolvido
   */
  @Column(name = "fine_amount")
  private Integer fineAmount;

  /**
   * Status da cobrança da multa
   * PENDING: multa pendente de pagamento
   * PAID: multa paga
   * FORGIVEN: multa perdoada
   * Null se não houver multa
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "fine_status")
  private FineStatus fineStatus;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public enum LoanStatus {
    ACTIVE,
    RETURNED,
    OVERDUE
  }

  public enum FineStatus {
    PENDING,
    PAID,
    FORGIVEN
  }
}
