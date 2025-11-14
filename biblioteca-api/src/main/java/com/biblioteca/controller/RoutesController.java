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
    loanRoutes.put("GET /api/loans/active/user/{userId}", Map.of(
        "description", "Empréstimos ativos de um usuário",
        "handler", "LoanController.getActiveLoansByUser(Long userId)"));
    loanRoutes.put("GET /api/loans/books/{isbn}/availability", Map.of(
        "description", "Verificar disponibilidade antes de emprestar",
        "handler", "LoanController.checkBookAvailability(String isbn)"));
    loanRoutes.put("GET /api/loans/users/{userId}/can-borrow", Map.of(
        "description", "Verificar se usuário pode emprestar",
        "handler", "LoanController.canUserBorrow(Long userId)"));
    loanRoutes.put("GET /api/loans/check-overdue", Map.of(
        "description", "Verificar e atualizar empréstimos em atraso",
        "handler", "LoanController.checkOverdueLoans()"));
    loanRoutes.put("GET /api/loans/overdue-notifications", Map.of(
        "description", "Notificações de empréstimos em atraso (usa LinkedList)",
        "handler", "LoanController.getOverdueNotifications()"));
    loanRoutes.put("POST /api/loans", Map.of(
        "description", "Criar novo empréstimo",
        "handler", "LoanController.createLoan(@RequestBody LoanRequestDTO request)",
        "body", "LoanRequestDTO { userId, bookIsbn }"));
    loanRoutes.put("PUT /api/loans/{loanId}/return", Map.of(
        "description", "Registrar devolução de livro",
        "handler", "LoanController.returnLoan(Long loanId)"));
    routes.put("loans", loanRoutes);

    // Rotas de Usuários
    Map<String, Object> userRoutes = new HashMap<>();
    userRoutes.put("GET /api/users", Map.of(
        "description", "Listar todos os usuários",
        "handler", "UserController.getAllUsers()"));
    userRoutes.put("GET /api/users/{id}", Map.of(
        "description", "Buscar usuário por ID",
        "handler", "UserController.getUserById(Long id)"));
    userRoutes.put("POST /api/users", Map.of(
        "description", "Criar novo usuário",
        "handler", "UserController.createUser(@RequestBody User user)",
        "body", "User { name, email, maxLoans }"));
    userRoutes.put("PUT /api/users/{id}", Map.of(
        "description", "Atualizar usuário",
        "handler", "UserController.updateUser(Long id, @RequestBody User user)"));
    userRoutes.put("DELETE /api/users/{id}", Map.of(
        "description", "Excluir usuário",
        "handler", "UserController.deleteUser(Long id)"));
    routes.put("users", userRoutes);

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
