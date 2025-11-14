package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Loan;
import com.biblioteca.model.User;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public LoanService(LoanRepository loanRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Verifica disponibilidade de livro antes de emprestar
     */
    public boolean isBookAvailable(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        return book.getQuantity() > 0;
    }

    /**
     * Verifica se o usuário pode fazer mais empréstimos
     */
    public boolean canUserBorrow(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long activeLoansCount = userRepository.countActiveLoansByUserId(userId);
        return activeLoansCount < user.getMaxLoans();
    }

    /**
     * Registra novo empréstimo de livro
     */
    @Transactional
    public LoanResponseDTO createLoan(LoanRequestDTO request) {
        // Verificar disponibilidade do livro
        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if (book.getQuantity() <= 0) {
            throw new RuntimeException("Livro não disponível para empréstimo");
        }

        // Verificar limite de empréstimos do usuário
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long activeLoansCount = userRepository.countActiveLoansByUserId(request.getUserId());
        if (activeLoansCount >= user.getMaxLoans()) {
            throw new RuntimeException("Usuário atingiu o limite máximo de empréstimos simultâneos");
        }

        // Criar empréstimo
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14)); // 14 dias para devolução
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        Loan savedLoan = loanRepository.save(loan);

        // Atualizar estoque do livro
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        return LoanResponseDTO.fromEntity(savedLoan);
    }

    /**
     * Registra devolução de livro
     */
    @Transactional
    public LoanResponseDTO returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new RuntimeException("Livro já foi devolvido");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(Loan.LoanStatus.RETURNED);

        // Atualizar estoque do livro
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        Loan savedLoan = loanRepository.save(loan);
        return LoanResponseDTO.fromEntity(savedLoan);
    }

    /**
     * Gera relatório de empréstimos ativos
     */
    public List<LoanResponseDTO> getActiveLoans() {
        List<Loan> activeLoans = loanRepository.findByStatus(Loan.LoanStatus.ACTIVE);
        return activeLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtém empréstimos ativos de um usuário
     */
    public List<LoanResponseDTO> getActiveLoansByUser(Long userId) {
        List<Loan> activeLoans = loanRepository.findActiveLoansByUserId(userId);
        return activeLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Verifica e atualiza empréstimos em atraso
     */
    @Transactional
    public List<LoanResponseDTO> checkAndUpdateOverdueLoans() {
        LocalDate today = LocalDate.now();
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(today);

        for (Loan loan : overdueLoans) {
            if (loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                loan.setStatus(Loan.LoanStatus.OVERDUE);
                loanRepository.save(loan);
                // Adicionar à fila de notificações usando LinkedList
                notificationService.addOverdueNotification(loan);
            }
        }

        return overdueLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtém notificações de empréstimos em atraso
     */
    public List<LoanResponseDTO> getOverdueNotifications() {
        List<Loan> overdueLoans = notificationService.getAllNotifications();
        return overdueLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtém todos os empréstimos
     */
    public List<LoanResponseDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
