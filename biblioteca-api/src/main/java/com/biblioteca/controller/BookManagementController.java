package com.biblioteca.controller;

import com.biblioteca.dto.BookAvailabilityDTO;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookManagementController {

    private final BookRepository bookRepository;
    private final BookService bookService;

    @Autowired
    public BookManagementController(BookRepository bookRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        // Verificar se já existe livro com esse ISBN
        if (bookRepository.existsById(book.getIsbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null); // ou retornar erro apropriado
        }
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    /**
     * Cadastrar múltiplos livros de uma vez
     * POST /api/books/batch
     * Aceita um array de livros no body
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createBooksBatch(@Valid @RequestBody List<Book> books) {
        Map<String, Object> response = new HashMap<>();
        List<Book> savedBooks = new ArrayList<>();
        List<Map<String, String>> errors = new ArrayList<>();

        for (Book book : books) {
            try {
                // Verificar se já existe livro com esse ISBN
                if (bookRepository.existsById(book.getIsbn())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("isbn", book.getIsbn());
                    error.put("title", book.getTitle());
                    error.put("message", "Livro com este ISBN já existe");
                    errors.add(error);
                    continue;
                }
                Book savedBook = bookRepository.save(book);
                savedBooks.add(savedBook);
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("isbn", book.getIsbn());
                error.put("title", book.getTitle());
                error.put("message", "Erro ao salvar: " + e.getMessage());
                errors.add(error);
            }
        }

        response.put("success", savedBooks.size());
        response.put("failed", errors.size());
        response.put("total", books.size());
        response.put("saved_books", savedBooks);

        if (!errors.isEmpty()) {
            response.put("errors", errors);
        }

        HttpStatus status = errors.isEmpty()
                ? HttpStatus.CREATED
                : (savedBooks.isEmpty() ? HttpStatus.BAD_REQUEST : HttpStatus.MULTI_STATUS);

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    /**
     * Verificar disponibilidade detalhada de um livro
     * GET /api/books/{isbn}/availability
     * Esta rota deve vir antes de /{isbn} para evitar conflito
     */
    @GetMapping("/{isbn}/availability")
    public ResponseEntity<BookAvailabilityDTO> checkBookAvailability(@PathVariable String isbn) {
        try {
            BookAvailabilityDTO availability = bookService.checkBookAvailability(isbn);
            return ResponseEntity.ok(availability);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookRepository.findById(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @Valid @RequestBody Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findById(isbn);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            // Não permite alterar o ISBN (chave primária)
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setCoverImageUrl(bookDetails.getCoverImageUrl());
            book.setKeywords(bookDetails.getKeywords());
            book.setSynopsis(bookDetails.getSynopsis());
            book.setQuantity(bookDetails.getQuantity());

            Book updatedBook = bookRepository.save(book);
            return ResponseEntity.ok(updatedBook);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        try {
            // Validar se pode deletar (verifica empréstimos e reservas)
            bookService.validateBookDeletion(isbn);

            // Se passou na validação, pode deletar
            bookRepository.deleteById(isbn);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Retornar mensagem de erro se não puder deletar
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
