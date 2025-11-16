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
  private Loan.FineStatus fineStatus;

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
    dto.setFineStatus(loan.getFineStatus());
    return dto;
  }
}
