package com.biblioteca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para listar todas as rotas disponíveis
 * Acesse GET /api/routes para ver todas as rotas
 */
@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RoutesController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoutes() {
        Map<String, Object> routes = new HashMap<>();

        // Rotas de Livros
        Map<String, Object> bookRoutes = new HashMap<>();
        bookRoutes.put("GET /api/books", Map.of(
                "description", "Listar todos os livros",
                "handler", "BookManagementController.getAllBooks()"));
        bookRoutes.put("GET /api/books/{isbn}", Map.of(
                "description", "Buscar livro por ISBN",
                "handler", "BookManagementController.getBookByIsbn(String isbn)"));
        bookRoutes.put("GET /api/books/{isbn}/availability", Map.of(
                "description", "Verificar disponibilidade do livro",
                "handler", "BookManagementController.checkBookAvailability(String isbn)"));
        bookRoutes.put("POST /api/books", Map.of(
                "description", "Criar novo livro (um por vez)",
                "handler", "BookManagementController.createBook(@RequestBody Book book)",
                "body", "Book { isbn, title, author, coverImageUrl, keywords, synopsis, quantity }"));
        bookRoutes.put("POST /api/books/batch", Map.of(
                "description", "Criar múltiplos livros de uma vez (array)",
                "handler", "BookManagementController.createBooksBatch(@RequestBody List<Book> books)",
                "body", "Array de Book [{ isbn, title, author, coverImageUrl, keywords, synopsis, quantity }, ...]"));
        bookRoutes.put("PUT /api/books/{isbn}", Map.of(
                "description", "Atualizar livro",
                "handler", "BookManagementController.updateBook(String isbn, @RequestBody Book book)"));
        bookRoutes.put("DELETE /api/books/{isbn}", Map.of(
                "description", "Excluir livro por ISBN",
                "handler", "BookManagementController.deleteBook(String isbn)"));
        routes.put("books", bookRoutes);

        // Rotas de Empréstimos
        Map<String, Object> loanRoutes = new HashMap<>();
        loanRoutes.put("GET /api/loans", Map.of(
                "description", "Listar todos os empréstimos",
                "handler", "LoanController.getAllLoans()"));
        loanRoutes.put("GET /api/loans/active", Map.of(
                "description", "Listar empréstimos ativos",
                "handler", "LoanController.getActiveLoans()"));
        loanRoutes.put("GET /api/loans/overdue", Map.of(
                "description", "Listar empréstimos em atraso (status OVERDUE)",
                "handler", "LoanController.getOverdueLoans()"));
        loanRoutes.put("GET /api/loans/returned", Map.of(
                "description", "Listar empréstimos devolvidos (status RETURNED)",
                "handler", "LoanController.getReturnedLoans()"));
        loanRoutes.put("GET /api/loans/active-and-overdue", Map.of(
                "description", "Listar empréstimos ativos e em atraso juntos (status ACTIVE e OVERDUE)",
                "handler", "LoanController.getActiveAndOverdueLoans()"));
        loanRoutes.put("GET /api/loans/active/student/{matricula}", Map.of(
                "description", "Empréstimos ativos de um aluno",
                "handler", "LoanController.getActiveLoansByStudent(String matricula)"));
        loanRoutes.put("GET /api/loans/books/{isbn}/availability", Map.of(
                "description", "Verificar disponibilidade antes de emprestar",
                "handler", "LoanController.checkBookAvailability(String isbn)"));
        loanRoutes.put("GET /api/loans/students/{matricula}/can-borrow", Map.of(
                "description", "Verificar se aluno pode emprestar",
                "handler", "LoanController.canStudentBorrow(String matricula)"));
        loanRoutes.put("GET /api/loans/check-overdue", Map.of(
                "description", "Verificar e atualizar empréstimos em atraso",
                "handler", "LoanController.checkOverdueLoans()"));
        loanRoutes.put("POST /api/loans", Map.of(
                "description", "Criar novo empréstimo",
                "handler", "LoanController.createLoan(@RequestBody LoanRequestDTO request)",
                "body", "LoanRequestDTO { studentMatricula, bookIsbn }"));
        loanRoutes.put("PUT /api/loans/{loanId}/return", Map.of(
                "description", "Registrar devolução de livro (data de devolução opcional no body)",
                "handler", "LoanController.returnLoan(Long loanId, LoanReturnDTO returnDTO)",
                "body", "LoanReturnDTO { returnDate (opcional) } - Se não informado, usa data atual"));
        routes.put("loans", loanRoutes);

        // Rotas de Reservas
        Map<String, Object> reservationRoutes = new HashMap<>();
        reservationRoutes.put("GET /api/reservations", Map.of(
                "description", "Listar todas as reservas",
                "handler", "ReservationController.getAllReservations()"));
        reservationRoutes.put("GET /api/reservations/{id}", Map.of(
                "description", "Buscar reserva por ID",
                "handler", "ReservationController.getReservationById(Long id)"));
        reservationRoutes.put("GET /api/reservations/book/{isbn}", Map.of(
                "description", "Listar reservas ativas de um livro (ordem da fila)",
                "handler", "ReservationController.getReservationsByBook(String isbn)"));
        reservationRoutes.put("GET /api/reservations/student/{matricula}", Map.of(
                "description", "Listar reservas ativas de um estudante",
                "handler", "ReservationController.getReservationsByStudent(String matricula)"));
        reservationRoutes.put("POST /api/reservations", Map.of(
                "description", "Criar nova reserva (máximo 5 por livro, fila ordenada)",
                "handler", "ReservationController.createReservation(@RequestBody ReservationRequestDTO request)",
                "body", "ReservationRequestDTO { bookIsbn, studentMatricula, reservationDate (opcional) }"));
        reservationRoutes.put("DELETE /api/reservations/{id}", Map.of(
                "description", "Cancelar reserva (reorganiza fila automaticamente)",
                "handler", "ReservationController.cancelReservation(Long id)"));
        reservationRoutes.put("PUT /api/reservations/{id}/fulfill", Map.of(
                "description", "Efetivar reserva (marcar como gerou empréstimo, reorganiza fila)",
                "handler", "ReservationController.fulfillReservation(Long id)"));
        routes.put("reservations", reservationRoutes);

        // Rotas de Notificações
        Map<String, Object> notificationRoutes = new HashMap<>();
        notificationRoutes.put("POST /api/notifications/overdue", Map.of(
                "description", "Enviar notificação de livro em atraso por e-mail",
                "handler",
                "NotificationController.sendOverdueNotification(@RequestBody OverdueNotificationDTO request)",
                "body", "OverdueNotificationDTO { loanId }"));
        notificationRoutes.put("POST /api/notifications/reservation-available", Map.of(
                "description", "Enviar notificação de livro reservado disponível por e-mail",
                "handler",
                "NotificationController.sendReservationAvailableNotification(@RequestBody ReservationAvailableNotificationDTO request)",
                "body", "ReservationAvailableNotificationDTO { reservationId }"));
        routes.put("notifications", notificationRoutes);

        // Rotas de Alunos
        Map<String, Object> studentRoutes = new HashMap<>();
        studentRoutes.put("GET /api/students", Map.of(
                "description", "Listar todos os alunos",
                "handler", "StudentController.getAllStudents()"));
        studentRoutes.put("GET /api/students/{matricula}", Map.of(
                "description", "Buscar aluno por matrícula",
                "handler", "StudentController.getStudentByMatricula(String matricula)"));
        studentRoutes.put("POST /api/students", Map.of(
                "description", "Criar novo aluno (um por vez)",
                "handler", "StudentController.createStudent(@RequestBody Student student)",
                "body", "Student { matricula, nome, cpf, dataNascimento }"));
        studentRoutes.put("POST /api/students/batch", Map.of(
                "description", "Criar múltiplos alunos de uma vez (array)",
                "handler", "StudentController.createStudentsBatch(@RequestBody List<Student> students)",
                "body", "Array de Student [{ matricula, nome, cpf, dataNascimento }, ...]"));
        studentRoutes.put("PUT /api/students/{matricula}", Map.of(
                "description", "Atualizar aluno",
                "handler", "StudentController.updateStudent(String matricula, @RequestBody Student student)"));
        studentRoutes.put("DELETE /api/students/{matricula}", Map.of(
                "description", "Excluir aluno por matrícula",
                "handler", "StudentController.deleteStudent(String matricula)"));
        routes.put("students", studentRoutes);

        // Rotas de Configurações
        Map<String, Object> settingsRoutes = new HashMap<>();
        settingsRoutes.put("GET /api/settings", Map.of(
                "description", "Obter configurações globais da biblioteca",
                "handler", "LibrarySettingsController.getSettings()"));
        settingsRoutes.put("PUT /api/settings", Map.of(
                "description", "Atualizar configurações globais",
                "handler", "LibrarySettingsController.updateSettings(@RequestBody LibrarySettings settings)",
                "body", "LibrarySettings { loanPeriodDays, maxLoansPerStudent }"));
        routes.put("settings", settingsRoutes);

        // Rotas de Sistema
        Map<String, Object> systemRoutes = new HashMap<>();
        systemRoutes.put("GET /", Map.of(
                "description", "Página inicial com informações da API",
                "handler", "IndexController.index()"));
        systemRoutes.put("GET /api/health", Map.of(
                "description", "Health check e status do banco de dados",
                "handler", "HealthController.health()"));
        systemRoutes.put("GET /api/routes", Map.of(
                "description", "Listar todas as rotas (este endpoint)",
                "handler", "RoutesController.getAllRoutes()"));
        routes.put("system", systemRoutes);

        Map<String, Object> response = new HashMap<>();
        response.put("total_routes", countRoutes(routes));
        response.put("routes", routes);
        response.put("documentation", "Veja ROUTES.md para documentação completa");

        return ResponseEntity.ok(response);
    }

    private int countRoutes(Map<String, Object> routes) {
        return routes.values().stream()
                .mapToInt(routeGroup -> {
                    if (routeGroup instanceof Map) {
                        return ((Map<?, ?>) routeGroup).size();
                    }
                    return 0;
                })
                .sum();
    }
}
