package com.biblioteca.dto;

/**
 * DTO que reúne métricas relacionadas aos estudantes.
 * Pode incluir informações como número de empréstimos por aluno,
 * média de devolução, histórico de atrasos e engajamento com a biblioteca.
 */

public class ReportStudentMetricsDTO {
    private Long totalStudents;
    private Long studentsWithActiveLoans;
    private Long studentsWithOverdueLoans;
    private Long studentsWithoutLoans;
    private Double averageLoansPerStudent;
    private Double averageOverdueDaysPerStudent;
    private Long totalActiveLoans;
    private Long totalOverdueLoans;

    // Constructors
    public ReportStudentMetricsDTO() {
    }

    public ReportStudentMetricsDTO(Long totalStudents, Long studentsWithActiveLoans,
                                    Long studentsWithOverdueLoans, Long studentsWithoutLoans,
                                    Double averageLoansPerStudent, Double averageOverdueDaysPerStudent,
                                    Long totalActiveLoans, Long totalOverdueLoans) {
        this.totalStudents = totalStudents;
        this.studentsWithActiveLoans = studentsWithActiveLoans;
        this.studentsWithOverdueLoans = studentsWithOverdueLoans;
        this.studentsWithoutLoans = studentsWithoutLoans;
        this.averageLoansPerStudent = averageLoansPerStudent;
        this.averageOverdueDaysPerStudent = averageOverdueDaysPerStudent;
        this.totalActiveLoans = totalActiveLoans;
        this.totalOverdueLoans = totalOverdueLoans;
    }

    // Getters e Setters
    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getStudentsWithActiveLoans() {
        return studentsWithActiveLoans;
    }

    public void setStudentsWithActiveLoans(Long studentsWithActiveLoans) {
        this.studentsWithActiveLoans = studentsWithActiveLoans;
    }

    public Long getStudentsWithOverdueLoans() {
        return studentsWithOverdueLoans;
    }

    public void setStudentsWithOverdueLoans(Long studentsWithOverdueLoans) {
        this.studentsWithOverdueLoans = studentsWithOverdueLoans;
    }

    public Long getStudentsWithoutLoans() {
        return studentsWithoutLoans;
    }

    public void setStudentsWithoutLoans(Long studentsWithoutLoans) {
        this.studentsWithoutLoans = studentsWithoutLoans;
    }

    public Double getAverageLoansPerStudent() {
        return averageLoansPerStudent;
    }

    public void setAverageLoansPerStudent(Double averageLoansPerStudent) {
        this.averageLoansPerStudent = averageLoansPerStudent;
    }

    public Double getAverageOverdueDaysPerStudent() {
        return averageOverdueDaysPerStudent;
    }

    public void setAverageOverdueDaysPerStudent(Double averageOverdueDaysPerStudent) {
        this.averageOverdueDaysPerStudent = averageOverdueDaysPerStudent;
    }

    public Long getTotalActiveLoans() {
        return totalActiveLoans;
    }

    public void setTotalActiveLoans(Long totalActiveLoans) {
        this.totalActiveLoans = totalActiveLoans;
    }

    public Long getTotalOverdueLoans() {
        return totalOverdueLoans;
    }

    public void setTotalOverdueLoans(Long totalOverdueLoans) {
        this.totalOverdueLoans = totalOverdueLoans;
    }
}
