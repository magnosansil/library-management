package com.biblioteca.controller;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

  private final LoanService loanService;

  @Autowired
  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  /**
   * Verificar disponibilidade de livro antes de emprestar (READ)
   * GET /api/loans/books/{isbn}/availability
   */
  @GetMapping("/books/{isbn}/availability")
  public ResponseEntity<Boolean> checkBookAvailability(@PathVariable String isbn) {
    try {
      boolean isAvailable = loanService.isBookAvailable(isbn);
      return ResponseEntity.ok(isAvailable);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }
  }

  /**
   * Verificar se aluno pode fazer mais empréstimos
   * GET /api/loans/students/{matricula}/can-borrow
   */
  @GetMapping("/students/{matricula}/can-borrow")
  public ResponseEntity<Boolean> canStudentBorrow(@PathVariable String matricula) {
    try {
      boolean canBorrow = loanService.canStudentBorrow(matricula);
      return ResponseEntity.ok(canBorrow);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }
  }

  /**
   * Registrar novo empréstimo de livro
   * POST /api/loans
   */
  @PostMapping
  public ResponseEntity<?> createLoan(@Valid @RequestBody LoanRequestDTO request) {
    try {
      LoanResponseDTO loan = loanService.createLoan(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao criar empréstimo: " + e.getMessage());
    }
  }

  /**
   * Registrar devolução de livro
   * PUT /api/loans/{loanId}/return
   */
  @PutMapping("/{loanId}/return")
  public ResponseEntity<?> returnLoan(@PathVariable Long loanId) {
    try {
      LoanResponseDTO loan = loanService.returnLoan(loanId);
      return ResponseEntity.ok(loan);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao devolver livro: " + e.getMessage());
    }
  }

  /**
   * Gerar relatório de empréstimos ativos
   * GET /api/loans/active
   */
  @GetMapping("/active")
  public ResponseEntity<List<LoanResponseDTO>> getActiveLoans() {
    List<LoanResponseDTO> loans = loanService.getActiveLoans();
    return ResponseEntity.ok(loans);
  }

  /**
   * Obter empréstimos ativos de um aluno
   * GET /api/loans/active/student/{matricula}
   */
  @GetMapping("/active/student/{matricula}")
  public ResponseEntity<List<LoanResponseDTO>> getActiveLoansByStudent(@PathVariable String matricula) {
    List<LoanResponseDTO> loans = loanService.getActiveLoansByStudent(matricula);
    return ResponseEntity.ok(loans);
  }

  /**
   * Verificar e atualizar empréstimos em atraso
   * GET /api/loans/check-overdue
   */
  @GetMapping("/check-overdue")
  public ResponseEntity<List<LoanResponseDTO>> checkOverdueLoans() {
    List<LoanResponseDTO> overdueLoans = loanService.checkAndUpdateOverdueLoans();
    return ResponseEntity.ok(overdueLoans);
  }

  /**
   * Notificar bibliotecário sobre empréstimos em atraso
   * GET /api/loans/overdue-notifications
   */
  @GetMapping("/overdue-notifications")
  public ResponseEntity<List<LoanResponseDTO>> getOverdueNotifications() {
    List<LoanResponseDTO> notifications = loanService.getOverdueNotifications();
    return ResponseEntity.ok(notifications);
  }

  /**
   * Obter todos os empréstimos
   * GET /api/loans
   */
  @GetMapping
  public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
    List<LoanResponseDTO> loans = loanService.getAllLoans();
    return ResponseEntity.ok(loans);
  }
}
