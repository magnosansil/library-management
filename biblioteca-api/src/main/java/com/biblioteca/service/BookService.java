package com.biblioteca.service;

import com.biblioteca.dto.BookAvailabilityDTO;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela gestão de livros no sistema.
 * Realiza operações como cadastro, atualização, exclusão e consulta de livros,
 * além de lidar com verificações de disponibilidade e lógica de negócio relacionada ao acervo.
 */

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public BookService(BookRepository bookRepository,
            LoanRepository loanRepository,
            ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
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

    /**
     * Verifica se um livro pode ser deletado
     * Não permite deletar se houver empréstimos ou reservas (em qualquer status)
     */
    public void validateBookDeletion(String isbn) {
        // Verificar se livro existe
        if (!bookRepository.existsById(isbn)) {
            throw new RuntimeException("Livro não encontrado");
        }

        // Verificar se há empréstimos para este livro
        Long loansCount = loanRepository.countLoansByBookIsbn(isbn);
        if (loansCount > 0) {
            throw new RuntimeException("Não é possível excluir o livro. Existem " + loansCount
                    + " empréstimo(s) registrado(s) para este livro.");
        }

        // Verificar se há reservas para este livro
        Long reservationsCount = reservationRepository.countReservationsByBookIsbn(isbn);
        if (reservationsCount > 0) {
            throw new RuntimeException("Não é possível excluir o livro. Existem " + reservationsCount
                    + " reserva(s) registrada(s) para este livro.");
        }
    }
}
