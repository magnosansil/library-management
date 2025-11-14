package com.biblioteca.dto;

import com.biblioteca.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
  private Long id;
  private Long userId;
  private String userName;
  private String bookIsbn;
  private String bookTitle;
  private String bookAuthor;
  private LocalDate loanDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private Loan.LoanStatus status;

  public static LoanResponseDTO fromEntity(Loan loan) {
    LoanResponseDTO dto = new LoanResponseDTO();
    dto.setId(loan.getId());
    dto.setUserId(loan.getUser().getId());
    dto.setUserName(loan.getUser().getName());
    dto.setBookIsbn(loan.getBook().getIsbn());
    dto.setBookTitle(loan.getBook().getTitle());
    dto.setBookAuthor(loan.getBook().getAuthor());
    dto.setLoanDate(loan.getLoanDate());
    dto.setDueDate(loan.getDueDate());
    dto.setReturnDate(loan.getReturnDate());
    dto.setStatus(loan.getStatus());
    return dto;
  }
}
