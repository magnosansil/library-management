package com.biblioteca.controller;

import com.biblioteca.dto.OverdueNotificationDTO;
import com.biblioteca.dto.ReservationAvailableNotificationDTO;
import com.biblioteca.model.Loan;
import com.biblioteca.model.Reservation;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.ReservationRepository;
import com.biblioteca.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para envio de notificações por e-mail
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

  private final EmailService emailService;
  private final LoanRepository loanRepository;
  private final ReservationRepository reservationRepository;

  @Autowired
  public NotificationController(
      EmailService emailService,
      LoanRepository loanRepository,
      ReservationRepository reservationRepository) {
    this.emailService = emailService;
    this.loanRepository = loanRepository;
    this.reservationRepository = reservationRepository;
  }

  /**
   * Enviar notificação de livro em atraso
   * POST /api/notifications/overdue
   */
  @PostMapping("/overdue")
  public ResponseEntity<?> sendOverdueNotification(@Valid @RequestBody OverdueNotificationDTO request) {
    try {
      Loan loan = loanRepository.findById(request.getLoanId())
          .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

      if (loan.getReturnDate() != null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Este empréstimo já foi devolvido");
      }

      if (loan.getStudent().getEmail() == null || loan.getStudent().getEmail().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Estudante não possui e-mail cadastrado");
      }

      // Calcular dias de atraso
      long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
          loan.getDueDate(),
          java.time.LocalDateTime.now());

      if (overdueDays <= 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Este empréstimo ainda não está em atraso");
      }

      // Enviar e-mail
      emailService.sendOverdueLoanNotification(
          loan.getStudent().getEmail(),
          loan.getStudent().getNome(),
          loan.getBook().getTitle(),
          loan.getBook().getIsbn(),
          (int) overdueDays);

      return ResponseEntity.ok(Map.of(
          "message", "Notificação de atraso enviada com sucesso",
          "email", loan.getStudent().getEmail(),
          "studentName", loan.getStudent().getNome(),
          "bookTitle", loan.getBook().getTitle(),
          "overdueDays", overdueDays));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao enviar notificação: " + e.getMessage());
    }
  }

  /**
   * Enviar notificação de livro reservado disponível
   * POST /api/notifications/reservation-available
   */
  @PostMapping("/reservation-available")
  public ResponseEntity<?> sendReservationAvailableNotification(
      @Valid @RequestBody ReservationAvailableNotificationDTO request) {
    try {
      Reservation reservation = reservationRepository.findById(request.getReservationId())
          .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

      if (reservation.getStatus() != Reservation.ReservationStatus.ACTIVE) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Esta reserva não está ativa");
      }

      if (reservation.getStudent().getEmail() == null || reservation.getStudent().getEmail().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Estudante não possui e-mail cadastrado");
      }

      // Verificar se o livro está disponível (quantidade > 0)
      if (reservation.getBook().getQuantity() <= 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("O livro ainda não está disponível");
      }

      // Enviar e-mail
      emailService.sendReservationAvailableNotification(
          reservation.getStudent().getEmail(),
          reservation.getStudent().getNome(),
          reservation.getBook().getTitle(),
          reservation.getBook().getIsbn());

      return ResponseEntity.ok(Map.of(
          "message", "Notificação de reserva disponível enviada com sucesso",
          "email", reservation.getStudent().getEmail(),
          "studentName", reservation.getStudent().getNome(),
          "bookTitle", reservation.getBook().getTitle(),
          "reservationId", reservation.getId()));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao enviar notificação: " + e.getMessage());
    }
  }
}
