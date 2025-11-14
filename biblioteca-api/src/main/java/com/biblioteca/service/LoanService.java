package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.dto.LoanReturnDTO;
import com.biblioteca.model.Book;
import com.biblioteca.model.Loan;
import com.biblioteca.model.Student;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;
    private final LibrarySettingsService settingsService;

    @Autowired
    public LoanService(LoanRepository loanRepository,
            BookRepository bookRepository,
            StudentRepository studentRepository,
            NotificationService notificationService,
            LibrarySettingsService settingsService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
        this.settingsService = settingsService;
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
     * Verifica se o aluno pode fazer mais empréstimos
     */
    public boolean canStudentBorrow(String matricula) {
        // Verificar se o aluno existe
        if (!studentRepository.existsById(matricula)) {
            throw new RuntimeException("Aluno não encontrado");
        }

        Integer maxLoans = settingsService.getMaxLoansPerStudent();
        Long activeLoansCount = studentRepository.countActiveLoansByMatricula(matricula);
        return activeLoansCount < maxLoans;
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

        // Verificar limite de empréstimos do aluno
        Student student = studentRepository.findById(request.getStudentMatricula())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Integer maxLoans = settingsService.getMaxLoansPerStudent();
        Long activeLoansCount = studentRepository.countActiveLoansByMatricula(request.getStudentMatricula());
        if (activeLoansCount >= maxLoans) {
            throw new RuntimeException(
                    "Aluno atingiu o limite máximo de empréstimos simultâneos (" + maxLoans + ")");
        }

        // Obter prazo de devolução das configurações
        Integer loanPeriodDays = settingsService.getLoanPeriodDays();

        // Definir data de empréstimo (usa a fornecida ou a atual)
        LocalDateTime loanDateTime = request.getLoanDate() != null
                ? request.getLoanDate()
                : LocalDateTime.now();

        // Calcular data de devolução (data de empréstimo + prazo configurado)
        LocalDateTime dueDateTime = loanDateTime.plusDays(loanPeriodDays);

        // Criar empréstimo
        Loan loan = new Loan();
        loan.setStudent(student);
        loan.setBook(book);
        loan.setLoanDate(loanDateTime);
        loan.setDueDate(dueDateTime);
        loan.setStatus(Loan.LoanStatus.ACTIVE);

        Loan savedLoan = loanRepository.save(loan);

        // Atualizar estoque do livro
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        return LoanResponseDTO.fromEntity(savedLoan);
    }

    /**
     * Registra devolução de livro
     * Calcula automaticamente os dias de atraso e o valor da multa
     * 
     * @param loanId    ID do empréstimo
     * @param returnDTO DTO com data de devolução (opcional). Se null ou sem
     *                  returnDate, usa data atual
     * @return DTO com os dados do empréstimo atualizado
     */
    @Transactional
    public LoanResponseDTO returnLoan(Long loanId, LoanReturnDTO returnDTO) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new RuntimeException("Livro já foi devolvido");
        }

        // Usar data fornecida ou data atual se não fornecida
        LocalDateTime returnDate = (returnDTO != null && returnDTO.getReturnDate() != null)
                ? returnDTO.getReturnDate()
                : LocalDateTime.now();

        loan.setReturnDate(returnDate);
        loan.setStatus(Loan.LoanStatus.RETURNED);

        // Calcular dias de atraso (diferença entre data de devolução e data de
        // vencimento)
        LocalDateTime dueDate = loan.getDueDate();
        long daysDifference = java.time.Duration.between(dueDate, returnDate).toDays();

        if (daysDifference > 0) {
            // Há atraso
            loan.setOverdueDays((int) daysDifference);

            // Calcular valor da multa: dias de atraso * multa por dia (das configurações)
            Integer finePerDay = settingsService.getFinePerDay();
            loan.setFineAmount((int) daysDifference * finePerDay);
        } else {
            // Sem atraso
            loan.setOverdueDays(0);
            loan.setFineAmount(0);
        }

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
     * Obtém empréstimos ativos de um aluno
     */
    public List<LoanResponseDTO> getActiveLoansByStudent(String matricula) {
        List<Loan> activeLoans = loanRepository.findActiveLoansByMatricula(matricula);
        return activeLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Verifica e atualiza empréstimos em atraso
     */
    @Transactional
    public List<LoanResponseDTO> checkAndUpdateOverdueLoans() {
        LocalDateTime now = LocalDateTime.now();
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(now);

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
