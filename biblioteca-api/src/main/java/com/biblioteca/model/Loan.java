package com.biblioteca.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
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

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Constructors
  public Loan() {
  }

  public Loan(Student student, Book book, LocalDateTime loanDate, LocalDateTime dueDate,
              LocalDateTime returnDate, LoanStatus status, Integer overdueDays, Integer fineAmount,
              LocalDateTime createdAt) {
    this.student = student;
    this.book = book;
    this.loanDate = loanDate;
    this.dueDate = dueDate;
    this.returnDate = returnDate;
    this.status = status;
    this.overdueDays = overdueDays;
    this.fineAmount = fineAmount;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }

  public LocalDateTime getLoanDate() {
    return loanDate;
  }

  public void setLoanDate(LocalDateTime loanDate) {
    this.loanDate = loanDate;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDateTime getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDateTime returnDate) {
    this.returnDate = returnDate;
  }

  public LoanStatus getStatus() {
    return status;
  }

  public void setStatus(LoanStatus status) {
    this.status = status;
  }

  public Integer getOverdueDays() {
    return overdueDays;
  }

  public void setOverdueDays(Integer overdueDays) {
    this.overdueDays = overdueDays;
  }

  public Integer getFineAmount() {
    return fineAmount;
  }

  public void setFineAmount(Integer fineAmount) {
    this.fineAmount = fineAmount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

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
