package com.biblioteca.service;

import com.biblioteca.dto.BookAvailabilityDTO;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o serviço de livros
 * Testa verificação de disponibilidade e informações de livros
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    public void setUp() {
        bookRepository.deleteAll();

        // Criar e salvar livro de teste
        testBook = new Book();
        testBook.setIsbn("978-1234567890");
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setQuantity(5);
        testBook.setActiveReservationsCount(0);
        bookRepository.save(testBook);
    }

    // ======================== TESTES DE DISPONIBILIDADE ========================

    @Test
    public void testCheckBookAvailabilityWhenAvailable() {
        // Arrange
        testBook.setQuantity(5);
        bookRepository.save(testBook);

        // Act
        BookAvailabilityDTO availability = bookService.checkBookAvailability(testBook.getIsbn());

        // Assert
        assertNotNull(availability);
        // Consider available if quantity > 0
        assertTrue(availability.getQuantity() > 0);
        assertEquals(5, availability.getQuantity());
    }

    @Test
    public void testCheckBookAvailabilityWhenOutOfStock() {
        // Arrange
        testBook.setQuantity(0);
        bookRepository.save(testBook);
        // Act
        BookAvailabilityDTO availability = bookService.checkBookAvailability(testBook.getIsbn());

        // Assert
        assertNotNull(availability);
        // Consider not available if quantity == 0
        assertFalse(availability.getQuantity() > 0);
        assertEquals(0, availability.getQuantity());
        assertEquals(0, availability.getQuantity());
    }

    @Test
    public void testCheckBookAvailabilityReturnsCorrectData() {
        // Act
        BookAvailabilityDTO availability = bookService.checkBookAvailability(testBook.getIsbn());

        // Assert
        assertNotNull(availability);
        assertEquals(testBook.getIsbn(), availability.getBookIsbn());
        assertEquals(testBook.getTitle(), availability.getBookTitle());
        assertEquals(testBook.getAuthor(), availability.getBookAuthor());
        assertEquals(testBook.getQuantity(), availability.getQuantity());
    }

    @Test
    public void testCheckBookAvailabilityThrowsExceptionForNonExistentBook() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            bookService.checkBookAvailability("978-9999999999");
        });
    }

    @Test
    public void testCheckBookAvailabilityWithDifferentQuantities() {
        // Test with quantity = 1
        testBook.setQuantity(1);
        bookRepository.save(testBook);
        BookAvailabilityDTO avail1 = bookService.checkBookAvailability(testBook.getIsbn());
        assertTrue(avail1.getQuantity() > 0);
        assertEquals(1, avail1.getQuantity());

        // Test with quantity = 10
        testBook.setQuantity(10);
        bookRepository.save(testBook);
        BookAvailabilityDTO avail10 = bookService.checkBookAvailability(testBook.getIsbn());
        assertTrue(avail10.getQuantity() > 0);
        assertEquals(10, avail10.getQuantity());
    }
}
