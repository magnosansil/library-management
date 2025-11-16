package com.biblioteca.controller;

import com.biblioteca.dto.ReportAvailabilityDTO;
import com.biblioteca.dto.ReportStudentMetricsDTO;
import com.biblioteca.dto.ReportLoanStatisticsDTO;
import com.biblioteca.dto.ReportReservationAnalyticsDTO;
import com.biblioteca.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para Relatórios
 * Fornece 4 novos endpoints para gerar relatórios úteis do sistema
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * RELATÓRIO 1: Disponibilidade do Acervo
     * 
     * Retorna estatísticas sobre disponibilidade de livros:
     * - Total de títulos
     * - Títulos com cópias disponíveis
     * - Títulos sem cópias disponíveis
     * - Percentual de disponibilidade
     * - Total de cópias em estoque
     * - Total de cópias realmente disponíveis
     * 
     * GET /api/reports/availability
     */
    @GetMapping("/availability")
    public ResponseEntity<ReportAvailabilityDTO> getAvailabilityReport() {
        try {
            ReportAvailabilityDTO report = reportService.generateAvailabilityReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * RELATÓRIO 2: Métricas de Alunos
     * 
     * Retorna estatísticas sobre atividades dos alunos:
     * - Total de alunos cadastrados
     * - Alunos com empréstimos ativos
     * - Alunos com empréstimos em atraso
     * - Alunos sem empréstimos
     * - Média de empréstimos por aluno
     * - Média de dias de atraso por aluno
     * - Total de empréstimos ativos
     * - Total de empréstimos em atraso
     * 
     * GET /api/reports/student-metrics
     */
    @GetMapping("/student-metrics")
    public ResponseEntity<ReportStudentMetricsDTO> getStudentMetricsReport() {
        try {
            ReportStudentMetricsDTO report = reportService.generateStudentMetricsReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * RELATÓRIO 3: Estatísticas de Empréstimos
     * 
     * Retorna análise completa de empréstimos:
     * - Total de empréstimos
     * - Empréstimos ativos
     * - Empréstimos devolvidos
     * - Empréstimos em atraso
     * - Percentuais de cada status
     * - Valor médio de multa
     * - Total de multas coletadas
     * - Duração média dos empréstimos
     * 
     * GET /api/reports/loan-statistics
     */
    @GetMapping("/loan-statistics")
    public ResponseEntity<ReportLoanStatisticsDTO> getLoanStatisticsReport() {
        try {
            ReportLoanStatisticsDTO report = reportService.generateLoanStatisticsReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * RELATÓRIO 4: Análise de Reservas
     * 
     * Retorna análise de padrões de reservas:
     * - Total de reservas registradas
     * - Reservas ativas
     * - Reservas efetivadas
     * - Reservas canceladas
     * - Taxa de efetivação
     * - Posição média na fila
     * - Total de livros com reservas
     * - Total de livros com fila cheia (5 posições)
     * - Tempo médio de espera
     * - Total de alunos com reservas
     * 
     * GET /api/reports/reservation-analytics
     */
    @GetMapping("/reservation-analytics")
    public ResponseEntity<ReportReservationAnalyticsDTO> getReservationAnalyticsReport() {
        try {
            ReportReservationAnalyticsDTO report = reportService.generateReservationAnalyticsReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
