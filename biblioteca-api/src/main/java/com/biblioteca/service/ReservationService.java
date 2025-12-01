package com.biblioteca.service;

import com.biblioteca.dto.ReservationRequestDTO;
import com.biblioteca.dto.ReservationResponseDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Reservation;
import com.biblioteca.model.Student;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.ReservationRepository;
import com.biblioteca.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Serviço responsável pelo gerenciamento de reservas de livros.
 *
 * <p>Centraliza as regras de negócio relacionadas à criação, validação,
 * listagem e controle da fila de reservas. O objetivo deste serviço é
 * garantir que alunos possam registrar interesse em livros indisponíveis,
 * respeitando limites e mantendo a ordem correta da fila.</p>
 *
 * <p>Principais responsabilidades:</p>
 * <ul>
 *     <li>Validar se o livro pode receber novas reservas 
 *     (máximo definido de {@code MAX_RESERVATIONS_PER_BOOK}).</li>
 *
 *     <li>Registrar novas reservas, sempre atribuindo a próxima posição
 *     disponível na fila.</li>
 *
 *     <li>Consultar reservas existentes por aluno, livro ou listagem geral.</li>
 *
 *     <li>Fornecer informações estruturadas para os DTOs utilizados no fluxo
 *     da aplicação.</li>
 * </ul>
 *
 * <p>Este serviço também utiliza operações transacionais para garantir
 * a consistência das reservas durante o processo de criação e validação.</p>
 */
@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final BookRepository bookRepository;
  private final StudentRepository studentRepository;
  private static final int MAX_RESERVATIONS_PER_BOOK = 5;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository,
      BookRepository bookRepository,
      StudentRepository studentRepository) {
    this.reservationRepository = reservationRepository;
    this.bookRepository = bookRepository;
    this.studentRepository = studentRepository;
  }

  /**
   * Cria uma nova reserva
   * Verifica se o livro pode ter mais reservas (máximo 5)
   * Adiciona na próxima posição disponível na fila
   */
  @Transactional
  public ReservationResponseDTO createReservation(ReservationRequestDTO request) {
    // Verificar se livro existe
    Book book = bookRepository.findById(request.getBookIsbn())
        .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

    // Verificar se estudante existe
    Student student = studentRepository.findById(request.getStudentMatricula())
        .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

    // Verificar se já tem reserva ativa para este livro
    if (reservationRepository.existsActiveReservationByStudentAndBook(
        request.getStudentMatricula(), request.getBookIsbn())) {
      throw new RuntimeException("Estudante já possui uma reserva ativa para este livro");
    }

    // Verificar se o livro já tem 5 reservas ativas (máximo)
    Long activeReservationsCount = reservationRepository.countActiveReservationsByBookIsbn(request.getBookIsbn());
    if (activeReservationsCount >= MAX_RESERVATIONS_PER_BOOK) {
      throw new RuntimeException("Livro já possui o número máximo de reservas (" + MAX_RESERVATIONS_PER_BOOK + ")");
    }

    // Calcular próxima posição na fila
    Integer maxPosition = reservationRepository.findMaxQueuePositionByBookIsbn(request.getBookIsbn());
    Integer nextPosition = maxPosition + 1;

    // Criar reserva
    Reservation reservation = new Reservation();
    reservation.setBook(book);
    reservation.setStudent(student);
    reservation.setReservationDate(request.getReservationDate() != null
        ? request.getReservationDate()
        : LocalDateTime.now());
    reservation.setQueuePosition(nextPosition);
    reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

    Reservation savedReservation = reservationRepository.save(reservation);

    // Atualizar contadores
    updateReservationCounters(book, student);

    return ReservationResponseDTO.fromEntity(savedReservation);
  }

  /**
   * Cancela uma reserva
   * Reorganiza as posições da fila (as reservas posteriores avançam)
   */
  @Transactional
  public void cancelReservation(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new RuntimeException("Reservação não encontrada"));

    if (reservation.getStatus() != Reservation.ReservationStatus.ACTIVE) {
      throw new RuntimeException("Apenas reservas ativas podem ser canceladas");
    }

    String bookIsbn = reservation.getBook().getIsbn();
    Integer cancelledPosition = reservation.getQueuePosition();

    // Marcar como cancelada
    reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
    reservationRepository.save(reservation);

    // Reorganizar fila: todas as reservas com posição maior que a cancelada avançam
    List<Reservation> reservationsToUpdate = reservationRepository
        .findActiveReservationsByBookIsbnOrderByPosition(bookIsbn)
        .stream()
        .filter(r -> r.getQueuePosition() > cancelledPosition)
        .collect(Collectors.toList());

    for (Reservation r : reservationsToUpdate) {
      r.setQueuePosition(r.getQueuePosition() - 1);
      reservationRepository.save(r);
    }

    // Atualizar contadores
    Book book = reservation.getBook();
    Student student = reservation.getStudent();
    updateReservationCounters(book, student);
  }

  /**
   * Marca uma reserva como efetivada (gerou empréstimo)
   * Remove da fila e reorganiza as posições
   */
  @Transactional
  public ReservationResponseDTO fulfillReservation(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new RuntimeException("Reservação não encontrada"));

    if (reservation.getStatus() != Reservation.ReservationStatus.ACTIVE) {
      throw new RuntimeException("Apenas reservas ativas podem ser efetivadas");
    }

    String bookIsbn = reservation.getBook().getIsbn();
    Integer fulfilledPosition = reservation.getQueuePosition();

    // Marcar como efetivada
    reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
    reservationRepository.save(reservation);

    // Reorganizar fila: todas as reservas com posição maior que a efetivada avançam
    List<Reservation> reservationsToUpdate = reservationRepository
        .findActiveReservationsByBookIsbnOrderByPosition(bookIsbn)
        .stream()
        .filter(r -> r.getQueuePosition() > fulfilledPosition)
        .collect(Collectors.toList());

    for (Reservation r : reservationsToUpdate) {
      r.setQueuePosition(r.getQueuePosition() - 1);
      reservationRepository.save(r);
    }

    // Atualizar contadores
    Book book = reservation.getBook();
    Student student = reservation.getStudent();
    updateReservationCounters(book, student);

    return ReservationResponseDTO.fromEntity(reservation);
  }

  /**
   * Lista todas as reservas ativas de um livro (ordem da fila)
   */
  public List<ReservationResponseDTO> getActiveReservationsByBook(String isbn) {
    List<Reservation> reservations = reservationRepository
        .findActiveReservationsByBookIsbnOrderByPosition(isbn);
    return reservations.stream()
        .map(ReservationResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * Lista todas as reservas ativas de um estudante
   */
  public List<ReservationResponseDTO> getActiveReservationsByStudent(String matricula) {
    List<Reservation> reservations = reservationRepository
        .findActiveReservationsByStudentMatricula(matricula);
    return reservations.stream()
        .map(ReservationResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * Lista todas as reservas
   */
  public List<ReservationResponseDTO> getAllReservations() {
    List<Reservation> reservations = reservationRepository.findAll();
    return reservations.stream()
        .map(ReservationResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * Busca uma reserva por ID
   */
  public ReservationResponseDTO getReservationById(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservação não encontrada"));
    return ReservationResponseDTO.fromEntity(reservation);
  }

  /**
   * Atualiza os contadores de reservas em Book e Student
   */
  @Transactional
  private void updateReservationCounters(Book book, Student student) {
    // Atualizar contador do livro (apenas reservas ativas)
    Long activeCount = reservationRepository.countActiveReservationsByBookIsbn(book.getIsbn());
    book.setActiveReservationsCount(activeCount.intValue());
    bookRepository.save(book);

    // Atualizar contador do estudante (total de reservas registradas, todas
    // incluindo canceladas/efetivadas)
    // Buscar todas as reservas do estudante no banco
    Long studentReservationsCount = reservationRepository.findAll().stream()
        .filter(r -> r.getStudent().getMatricula().equals(student.getMatricula()))
        .count();
    student.setReservationsCount(studentReservationsCount.intValue());
    studentRepository.save(student);
  }

  /**
   * Atualiza contadores após operações que não envolvem criação
   */
  @Transactional
  public void updateCountersForBook(String isbn) {
    Book book = bookRepository.findById(isbn)
        .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    Long activeCount = reservationRepository.countActiveReservationsByBookIsbn(isbn);
    book.setActiveReservationsCount(activeCount.intValue());
    bookRepository.save(book);
  }
}
