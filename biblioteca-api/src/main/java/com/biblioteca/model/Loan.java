package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_isbn", nullable = false)
  private Book book;

  @Column(name = "loan_date", nullable = false)
  private LocalDate loanDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "return_date")
  private LocalDate returnDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoanStatus status = LoanStatus.ACTIVE;

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
}
