package com.biblioteca.service;

import com.biblioteca.dto.ReservationRequestDTO;
import com.biblioteca.dto.ReservationResponseDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Reservation;
import com.biblioteca.model.Student;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.ReservationRepository;
import com.biblioteca.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o serviço de reservas
 * Testa fila de reservas, limite máximo, reorganização automática e contadores
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Book testBook;
    private Student student1, student2, student3;

    @BeforeEach
    public void setUp() {
        // Limpar dados anteriores
        reservationRepository.deleteAll();
        bookRepository.deleteAll();
        studentRepository.deleteAll();

        // Criar e salvar livro de teste
        testBook = new Book();
        testBook.setIsbn("978-1234567890");
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setQuantity(5);
        testBook.setActiveReservationsCount(0);
        bookRepository.save(testBook);

        // Criar e salvar alunos para testes
        student1 = createStudent("MAT001", "João Silva", "12345678901", "joao@test.com");
        student2 = createStudent("MAT002", "Maria Santos", "12345678902", "maria@test.com");
        student3 = createStudent("MAT003", "Pedro Costa", "12345678903", "pedro@test.com");
    }

    private Student createStudent(String matricula, String nome, String cpf, String email) {
        Student student = new Student();
        student.setMatricula(matricula);
        student.setNome(nome);
        student.setCpf(cpf);
        student.setDataNascimento(LocalDate.of(2000, 1, 1));
        student.setEmail(email);
        student.setReservationsCount(0);
        return studentRepository.save(student);
    }

    // ======================== TESTES DE CRIAÇÃO DE RESERVAS ========================

    @Test
    public void testCreateReservationSuccessfully() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());

        // Act
        ReservationResponseDTO response = reservationService.createReservation(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getQueuePosition());
        assertEquals(Reservation.ReservationStatus.ACTIVE, response.getStatus());
    }

    @Test
    public void testCreateMultipleReservationsForSameBook() {
        // Arrange
        ReservationRequestDTO request1 = new ReservationRequestDTO();
        request1.setBookIsbn(testBook.getIsbn());
        request1.setStudentMatricula(student1.getMatricula());

        ReservationRequestDTO request2 = new ReservationRequestDTO();
        request2.setBookIsbn(testBook.getIsbn());
        request2.setStudentMatricula(student2.getMatricula());

        ReservationRequestDTO request3 = new ReservationRequestDTO();
        request3.setBookIsbn(testBook.getIsbn());
        request3.setStudentMatricula(student3.getMatricula());

        // Act
        ReservationResponseDTO res1 = reservationService.createReservation(request1);
        ReservationResponseDTO res2 = reservationService.createReservation(request2);
        ReservationResponseDTO res3 = reservationService.createReservation(request3);

        // Assert
        assertEquals(1, res1.getQueuePosition());
        assertEquals(2, res2.getQueuePosition());
        assertEquals(3, res3.getQueuePosition());
    }

    @Test
    public void testCreateReservationWithMaximumFive() {
        // Arrange & Act - Criar 5 reservas (deve funcionar)
        for (int i = 0; i < 5; i++) {
            ReservationRequestDTO request = new ReservationRequestDTO();
            request.setBookIsbn(testBook.getIsbn());
            request.setStudentMatricula("MAT00" + (i + 1));

            // Criar estudante se não existir
            if (i >= 1) {
                Student student = new Student();
                student.setMatricula("MAT00" + (i + 1));
                student.setNome("Student " + (i + 1));
                student.setCpf("1234567890" + (i + 1));
                student.setDataNascimento(LocalDate.of(2000, 1, 1));
                student.setEmail("student" + (i + 1) + "@test.com");
                student.setReservationsCount(0);
                studentRepository.save(student);
            }

            ReservationResponseDTO response = reservationService.createReservation(request);
            assertNotNull(response);
        }

        // Assert - Tentar criar 6ª (deve falhar)
        assertThrows(RuntimeException.class, () -> {
            ReservationRequestDTO request = new ReservationRequestDTO();
            request.setBookIsbn(testBook.getIsbn());
            request.setStudentMatricula("MAT006");

            Student student = new Student();
            student.setMatricula("MAT006");
            student.setNome("Student 6");
            student.setCpf("12345678906");
            student.setDataNascimento(LocalDate.of(2000, 1, 1));
            student.setEmail("student6@test.com");
            student.setReservationsCount(0);
            studentRepository.save(student);

            reservationService.createReservation(request);
        });
    }

    @Test
    public void testPreventDuplicateReservationForSameStudent() {
        // Arrange
        ReservationRequestDTO request1 = new ReservationRequestDTO();
        request1.setBookIsbn(testBook.getIsbn());
        request1.setStudentMatricula(student1.getMatricula());

        reservationService.createReservation(request1);

        // Act & Assert - Tentar criar segunda reserva do mesmo estudante para o mesmo livro
        assertThrows(RuntimeException.class, () -> {
            ReservationRequestDTO request2 = new ReservationRequestDTO();
            request2.setBookIsbn(testBook.getIsbn());
            request2.setStudentMatricula(student1.getMatricula());
            reservationService.createReservation(request2);
        });
    }

    @Test
    public void testCreateReservationWithCustomDate() {
        // Arrange
        LocalDateTime customDate = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());
        request.setReservationDate(customDate);

        // Act
        ReservationResponseDTO response = reservationService.createReservation(request);

        // Assert
        assertNotNull(response);
        // Verificar se usou a data customizada
        Reservation reservation = reservationRepository.findById(response.getId()).orElse(null);
        assertNotNull(reservation);
        assertEquals(customDate, reservation.getReservationDate());
    }

    // ======================== TESTES DE CANCELAMENTO ========================

    @Test
    public void testCancelReservationRemovesFromQueue() {
        // Arrange
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO res1 = reservationService.createReservation(req1);

        // Act
        reservationService.cancelReservation(res1.getId());

        // Assert
        Reservation cancelled = reservationRepository.findById(res1.getId()).orElse(null);
        assertNotNull(cancelled);
        assertEquals(Reservation.ReservationStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    public void testCancelReservationReorganizesQueue() {
        // Arrange - Criar 3 reservas
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO res1 = reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(testBook.getIsbn());
        req2.setStudentMatricula(student2.getMatricula());
        ReservationResponseDTO res2 = reservationService.createReservation(req2);

        ReservationRequestDTO req3 = new ReservationRequestDTO();
        req3.setBookIsbn(testBook.getIsbn());
        req3.setStudentMatricula(student3.getMatricula());
        ReservationResponseDTO res3 = reservationService.createReservation(req3);

        // Act - Cancelar a do meio (posição 2)
        reservationService.cancelReservation(res2.getId());

        // Assert
        List<ReservationResponseDTO> remaining = reservationService
                .getActiveReservationsByBook(testBook.getIsbn());
        assertEquals(2, remaining.size());

        // res1 deve continuar em posição 1
        Reservation res1Db = reservationRepository.findById(res1.getId()).orElse(null);
        assertNotNull(res1Db);
        assertEquals(1, res1Db.getQueuePosition());

        // res3 deve ter mudado de posição 3 para 2
        Reservation res3Db = reservationRepository.findById(res3.getId()).orElse(null);
        assertNotNull(res3Db);
        assertEquals(2, res3Db.getQueuePosition());
    }

    // ======================== TESTES DE EFETIVAÇÃO ========================

    @Test
    public void testFulfillReservationMarksAsFulfilled() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO response = reservationService.createReservation(request);

        // Act
        ReservationResponseDTO fulfilled = reservationService.fulfillReservation(response.getId());

        // Assert
        assertEquals(Reservation.ReservationStatus.FULFILLED, fulfilled.getStatus());
    }

    @Test
    public void testFulfillReservationReorganizesQueue() {
        // Arrange - Criar 3 reservas
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO res1 = reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(testBook.getIsbn());
        req2.setStudentMatricula(student2.getMatricula());
        ReservationResponseDTO res2 = reservationService.createReservation(req2);

        ReservationRequestDTO req3 = new ReservationRequestDTO();
        req3.setBookIsbn(testBook.getIsbn());
        req3.setStudentMatricula(student3.getMatricula());
        ReservationResponseDTO res3 = reservationService.createReservation(req3);

        // Act - Efetivar a posição 1
        reservationService.fulfillReservation(res1.getId());

        // Assert
        List<ReservationResponseDTO> remaining = reservationService
                .getActiveReservationsByBook(testBook.getIsbn());
        assertEquals(2, remaining.size());

        // res2 deve ter mudado de posição 2 para 1
        Reservation res2Db = reservationRepository.findById(res2.getId()).orElse(null);
        assertNotNull(res2Db);
        assertEquals(1, res2Db.getQueuePosition());

        // res3 deve ter mudado de posição 3 para 2
        Reservation res3Db = reservationRepository.findById(res3.getId()).orElse(null);
        assertNotNull(res3Db);
        assertEquals(2, res3Db.getQueuePosition());
    }

    // ======================== TESTES DE CONSULTAS ========================

    @Test
    public void testGetActiveReservationsByBook() {
        // Arrange
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(testBook.getIsbn());
        req2.setStudentMatricula(student2.getMatricula());
        reservationService.createReservation(req2);

        // Act
        List<ReservationResponseDTO> active = reservationService
                .getActiveReservationsByBook(testBook.getIsbn());

        // Assert
        assertEquals(2, active.size());
        // Verificar se estão em ordem correta
        assertEquals(1, active.get(0).getQueuePosition());
        assertEquals(2, active.get(1).getQueuePosition());
    }

    @Test
    public void testGetActiveReservationsByStudent() {
        // Arrange
        Book book2 = new Book();
        book2.setIsbn("978-1234567891");
        book2.setTitle("Test Book 2");
        book2.setAuthor("Test Author 2");
        book2.setQuantity(5);
        book2.setActiveReservationsCount(0);
        bookRepository.save(book2);

        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(book2.getIsbn());
        req2.setStudentMatricula(student1.getMatricula());
        reservationService.createReservation(req2);

        // Act
        List<ReservationResponseDTO> studentReservations = reservationService
                .getActiveReservationsByStudent(student1.getMatricula());

        // Assert
        assertEquals(2, studentReservations.size());
    }

    @Test
    public void testGetReservationById() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO created = reservationService.createReservation(request);

        // Act
        ReservationResponseDTO retrieved = reservationService.getReservationById(created.getId());

        // Assert
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getBookIsbn(), retrieved.getBookIsbn());
        assertEquals(created.getStudentMatricula(), retrieved.getStudentMatricula());
    }

    @Test
    public void testGetAllReservations() {
        // Arrange
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(testBook.getIsbn());
        req2.setStudentMatricula(student2.getMatricula());
        reservationService.createReservation(req2);

        // Act
        List<ReservationResponseDTO> all = reservationService.getAllReservations();

        // Assert
        assertTrue(all.size() >= 2);
    }

    // ======================== TESTES DE CONTADORES ========================

    @Test
    public void testActiveReservationsCountIncrementsOnCreation() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());

        // Act
        reservationService.createReservation(request);

        // Assert
        Book updatedBook = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(updatedBook);
        assertEquals(1, updatedBook.getActiveReservationsCount());
    }

    @Test
    public void testActiveReservationsCountDecrementsOnCancellation() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO created = reservationService.createReservation(request);

        Book bookAfterCreation = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(bookAfterCreation);
        assertEquals(1, bookAfterCreation.getActiveReservationsCount());

        // Act
        reservationService.cancelReservation(created.getId());

        // Assert
        Book bookAfterCancel = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(bookAfterCancel);
        assertEquals(0, bookAfterCancel.getActiveReservationsCount());
    }

    @Test
    public void testActiveReservationsCountDecrementsOnFulfillment() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO created = reservationService.createReservation(request);

        Book bookAfterCreation = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(bookAfterCreation);
        assertEquals(1, bookAfterCreation.getActiveReservationsCount());

        // Act
        reservationService.fulfillReservation(created.getId());

        // Assert
        Book bookAfterFulfill = bookRepository.findById(testBook.getIsbn()).orElse(null);
        assertNotNull(bookAfterFulfill);
        assertEquals(0, bookAfterFulfill.getActiveReservationsCount());
    }

    @Test
    public void testStudentReservationsCountIncrementsOnCreation() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setBookIsbn(testBook.getIsbn());
        request.setStudentMatricula(student1.getMatricula());

        // Act
        reservationService.createReservation(request);

        // Assert
        Student updatedStudent = studentRepository.findById(student1.getMatricula()).orElse(null);
        assertNotNull(updatedStudent);
        assertEquals(1, updatedStudent.getReservationsCount());
    }

    @Test
    public void testStudentReservationsCountIncludesAllReservations() {
        // Arrange
        ReservationRequestDTO req1 = new ReservationRequestDTO();
        req1.setBookIsbn(testBook.getIsbn());
        req1.setStudentMatricula(student1.getMatricula());
        ReservationResponseDTO res1 = reservationService.createReservation(req1);

        ReservationRequestDTO req2 = new ReservationRequestDTO();
        req2.setBookIsbn(testBook.getIsbn());
        req2.setStudentMatricula(student2.getMatricula());
        reservationService.createReservation(req2);

        // Act
        reservationService.cancelReservation(res1.getId());

        // Assert
        // student1 criou uma reserva e depois cancelou, então count deve ser 1
        Student updatedStudent = studentRepository.findById(student1.getMatricula()).orElse(null);
        assertNotNull(updatedStudent);
        assertEquals(1, updatedStudent.getReservationsCount());
    }
}
