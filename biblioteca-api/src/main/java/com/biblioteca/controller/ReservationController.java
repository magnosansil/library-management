package com.biblioteca.controller;

import com.biblioteca.dto.ReservationRequestDTO;
import com.biblioteca.dto.ReservationResponseDTO;
import com.biblioteca.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

  private final ReservationService reservationService;

  @Autowired
  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  /**
   * Criar nova reserva
   * POST /api/reservations
   */
  @PostMapping
  public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequestDTO request) {
    try {
      ReservationResponseDTO reservation = reservationService.createReservation(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao criar reserva: " + e.getMessage());
    }
  }

  /**
   * Listar todas as reservas
   * GET /api/reservations
   */
  @GetMapping
  public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
    List<ReservationResponseDTO> reservations = reservationService.getAllReservations();
    return ResponseEntity.ok(reservations);
  }

  /**
   * Buscar reserva por ID
   * GET /api/reservations/{id}
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getReservationById(@PathVariable Long id) {
    try {
      ReservationResponseDTO reservation = reservationService.getReservationById(id);
      return ResponseEntity.ok(reservation);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Reservação não encontrada: " + e.getMessage());
    }
  }

  /**
   * Listar reservas ativas de um livro (ordem da fila)
   * GET /api/reservations/book/{isbn}
   */
  @GetMapping("/book/{isbn}")
  public ResponseEntity<List<ReservationResponseDTO>> getReservationsByBook(@PathVariable String isbn) {
    List<ReservationResponseDTO> reservations = reservationService.getActiveReservationsByBook(isbn);
    return ResponseEntity.ok(reservations);
  }

  /**
   * Listar reservas ativas de um estudante
   * GET /api/reservations/student/{matricula}
   */
  @GetMapping("/student/{matricula}")
  public ResponseEntity<List<ReservationResponseDTO>> getReservationsByStudent(@PathVariable String matricula) {
    List<ReservationResponseDTO> reservations = reservationService.getActiveReservationsByStudent(matricula);
    return ResponseEntity.ok(reservations);
  }

  /**
   * Cancelar reserva
   * DELETE /api/reservations/{id}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
    try {
      reservationService.cancelReservation(id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao cancelar reserva: " + e.getMessage());
    }
  }

  /**
   * Efetivar reserva (marcar como gerou empréstimo)
   * PUT /api/reservations/{id}/fulfill
   */
  @PutMapping("/{id}/fulfill")
  public ResponseEntity<?> fulfillReservation(@PathVariable Long id) {
    try {
      ReservationResponseDTO reservation = reservationService.fulfillReservation(id);
      return ResponseEntity.ok(reservation);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Erro ao efetivar reserva: " + e.getMessage());
    }
  }
}
