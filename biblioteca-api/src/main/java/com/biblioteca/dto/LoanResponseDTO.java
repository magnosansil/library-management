package com.biblioteca.dto;

import com.biblioteca.model.Loan;

import java.time.LocalDateTime;

public class LoanResponseDTO {
  private Long id;
  private String studentMatricula;
  private String studentName;
  private String bookIsbn;
  private String bookTitle;
  private String bookAuthor;
  private LocalDateTime loanDate;
  private LocalDateTime dueDate;
  private LocalDateTime returnDate;
  private Loan.LoanStatus status;
  private Integer overdueDays;
  private Integer fineAmount;
  private Integer quantity;

  // Constructors
  public LoanResponseDTO() {
  }

  public LoanResponseDTO(Long id, String studentMatricula, String studentName, String bookIsbn,
                        String bookTitle, String bookAuthor, LocalDateTime loanDate, LocalDateTime dueDate,
                        LocalDateTime returnDate, Loan.LoanStatus status, Integer overdueDays, Integer fineAmount,
                        Integer quantity) {
    this.id = id;
    this.studentMatricula = studentMatricula;
    this.studentName = studentName;
    this.bookIsbn = bookIsbn;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.loanDate = loanDate;
    this.dueDate = dueDate;
    this.returnDate = returnDate;
    this.status = status;
    this.overdueDays = overdueDays;
    this.fineAmount = fineAmount;
    this.quantity = quantity;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStudentMatricula() {
    return studentMatricula;
  }

  public void setStudentMatricula(String studentMatricula) {
    this.studentMatricula = studentMatricula;
  }

  public String getStudentName() {
    return studentName;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  public String getBookIsbn() {
    return bookIsbn;
  }

  public void setBookIsbn(String bookIsbn) {
    this.bookIsbn = bookIsbn;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }

  public String getBookAuthor() {
    return bookAuthor;
  }

  public void setBookAuthor(String bookAuthor) {
    this.bookAuthor = bookAuthor;
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

  public Loan.LoanStatus getStatus() {
    return status;
  }

  public void setStatus(Loan.LoanStatus status) {
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

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public static LoanResponseDTO fromEntity(Loan loan) {
    LoanResponseDTO dto = new LoanResponseDTO();
    dto.setId(loan.getId());
    dto.setStudentMatricula(loan.getStudent().getMatricula());
    dto.setStudentName(loan.getStudent().getNome());
    dto.setBookIsbn(loan.getBook().getIsbn());
    dto.setBookTitle(loan.getBook().getTitle());
    dto.setBookAuthor(loan.getBook().getAuthor());
    dto.setLoanDate(loan.getLoanDate());
    dto.setDueDate(loan.getDueDate());
    dto.setReturnDate(loan.getReturnDate());
    dto.setStatus(loan.getStatus());
    dto.setOverdueDays(loan.getOverdueDays());
    dto.setFineAmount(loan.getFineAmount());
    dto.setQuantity(loan.getBook().getQuantity());
    return dto;
  }
}
