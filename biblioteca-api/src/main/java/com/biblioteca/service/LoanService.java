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
     * Atualiza o status do empréstimo automaticamente baseado nas datas
     * 
     * Regras:
     * - Se returnDate != null → RETURNED
     * - Se returnDate == null && dueDate < now → OVERDUE
     * - Se returnDate == null && dueDate >= now → ACTIVE
     * 
     * @param loan Empréstimo a ser atualizado
     * @return true se o status foi alterado, false caso contrário
     */
    @Transactional
    public boolean updateLoanStatus(Loan loan) {
        LocalDateTime now = LocalDateTime.now();
        Loan.LoanStatus currentStatus = loan.getStatus();
        Loan.LoanStatus newStatus;

        // Se já foi devolvido, sempre RETURNED
        if (loan.getReturnDate() != null) {
            newStatus = Loan.LoanStatus.RETURNED;
        }
        // Se não foi devolvido e a data de vencimento passou, está em atraso
        else if (loan.getDueDate().isBefore(now)) {
            newStatus = Loan.LoanStatus.OVERDUE;
        }
        // Se não foi devolvido e ainda não venceu, está ativo
        else {
            newStatus = Loan.LoanStatus.ACTIVE;
        }

        // Atualizar status se mudou
        if (currentStatus != newStatus) {
            loan.setStatus(newStatus);
            loanRepository.save(loan);

            // Se mudou para OVERDUE, adicionar notificação
            if (newStatus == Loan.LoanStatus.OVERDUE) {
                notificationService.addOverdueNotification(loan);
            }

            return true;
        }

        return false;
    }

    /**
     * Atualiza o status de uma lista de empréstimos
     */
    @Transactional
    public void updateLoansStatus(List<Loan> loans) {
        for (Loan loan : loans) {
            updateLoanStatus(loan);
        }
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
     * Atualiza status automaticamente antes de verificar
     */
    public boolean canStudentBorrow(String matricula) {
        // Verificar se o aluno existe
        if (!studentRepository.existsById(matricula)) {
            throw new RuntimeException("Aluno não encontrado");
        }

        // Atualizar status dos empréstimos do aluno antes de contar
        List<Loan> studentLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getStudent().getMatricula().equals(matricula)
                        && loan.getReturnDate() == null)
                .collect(Collectors.toList());
        updateLoansStatus(studentLoans);

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

        // Atualizar status dos empréstimos do aluno antes de verificar limite
        List<Loan> studentLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getStudent().getMatricula().equals(request.getStudentMatricula())
                        && loan.getReturnDate() == null)
                .collect(Collectors.toList());
        updateLoansStatus(studentLoans);

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

        // Garantir que o status está correto após salvar
        updateLoanStatus(savedLoan);

        return LoanResponseDTO.fromEntity(savedLoan);
    }

    /**
     * Gera relatório de empréstimos ativos
     * Atualiza status automaticamente antes de retornar
     */
    public List<LoanResponseDTO> getActiveLoans() {
        // Buscar todos os empréstimos que não foram devolvidos
        List<Loan> allLoans = loanRepository.findAll();
        updateLoansStatus(allLoans);

        // Filtrar apenas os que estão ativos após atualização
        List<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .collect(Collectors.toList());

        return activeLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtém empréstimos ativos de um aluno
     * Atualiza status automaticamente antes de retornar
     */
    public List<LoanResponseDTO> getActiveLoansByStudent(String matricula) {
        // Buscar todos os empréstimos do aluno que não foram devolvidos
        List<Loan> studentLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getStudent().getMatricula().equals(matricula)
                        && loan.getReturnDate() == null)
                .collect(Collectors.toList());

        updateLoansStatus(studentLoans);

        // Filtrar apenas os que estão ativos após atualização
        List<Loan> activeLoans = studentLoans.stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .collect(Collectors.toList());

        return activeLoans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Verifica e atualiza empréstimos em atraso
     * Atualiza status automaticamente de todos os empréstimos
     */
    @Transactional
    public List<LoanResponseDTO> checkAndUpdateOverdueLoans() {
        // Buscar todos os empréstimos não devolvidos
        List<Loan> allLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());

        // Atualizar status de todos
        updateLoansStatus(allLoans);

        // Filtrar apenas os que estão em atraso
        List<Loan> overdueLoans = allLoans.stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.OVERDUE)
                .collect(Collectors.toList());

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
     * Atualiza status automaticamente antes de retornar
     */
    public List<LoanResponseDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        updateLoansStatus(loans);
        return loans.stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
