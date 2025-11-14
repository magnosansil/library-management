package com.biblioteca.service;

import com.biblioteca.dto.BookAvailabilityDTO;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Verifica disponibilidade de um livro
     */
    public BookAvailabilityDTO checkBookAvailability(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        BookAvailabilityDTO dto = new BookAvailabilityDTO();
        dto.setBookIsbn(book.getIsbn());
        dto.setBookTitle(book.getTitle());
        dto.setBookAuthor(book.getAuthor());
        dto.setQuantity(book.getQuantity());
        dto.setIsAvailable(book.getQuantity() > 0);

        return dto;
    }
}
