package com.biblioteca.dto;

/**
 * DTO para relatório de estatísticas de empréstimos
 * Fornece análise completa de empréstimos incluindo distribuição por status e métricas
 */
public class ReportLoanStatisticsDTO {
    private Long totalLoans;
    private Long activeLoans;
    private Long returnedLoans;
    private Long overdueLoans;
    private Double activeLoansPercentage;
    private Double returnedLoansPercentage;
    private Double overdueLoansPercentage;
    private Double averageOverdueValue;
    private Long totalFinesCollected;
    private Double averageLoanDurationDays;

    // Constructors
    public ReportLoanStatisticsDTO() {
    }

    public ReportLoanStatisticsDTO(Long totalLoans, Long activeLoans, Long returnedLoans,
                                    Long overdueLoans, Double activeLoansPercentage,
                                    Double returnedLoansPercentage, Double overdueLoansPercentage,
                                    Double averageOverdueValue, Long totalFinesCollected,
                                    Double averageLoanDurationDays) {
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
        this.returnedLoans = returnedLoans;
        this.overdueLoans = overdueLoans;
        this.activeLoansPercentage = activeLoansPercentage;
        this.returnedLoansPercentage = returnedLoansPercentage;
        this.overdueLoansPercentage = overdueLoansPercentage;
        this.averageOverdueValue = averageOverdueValue;
        this.totalFinesCollected = totalFinesCollected;
        this.averageLoanDurationDays = averageLoanDurationDays;
    }

    // Getters e Setters
    public Long getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(Long totalLoans) {
        this.totalLoans = totalLoans;
    }

    public Long getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(Long activeLoans) {
        this.activeLoans = activeLoans;
    }

    public Long getReturnedLoans() {
        return returnedLoans;
    }

    public void setReturnedLoans(Long returnedLoans) {
        this.returnedLoans = returnedLoans;
    }

    public Long getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(Long overdueLoans) {
        this.overdueLoans = overdueLoans;
    }

    public Double getActiveLoansPercentage() {
        return activeLoansPercentage;
    }

    public void setActiveLoansPercentage(Double activeLoansPercentage) {
        this.activeLoansPercentage = activeLoansPercentage;
    }

    public Double getReturnedLoansPercentage() {
        return returnedLoansPercentage;
    }

    public void setReturnedLoansPercentage(Double returnedLoansPercentage) {
        this.returnedLoansPercentage = returnedLoansPercentage;
    }

    public Double getOverdueLoansPercentage() {
        return overdueLoansPercentage;
    }

    public void setOverdueLoansPercentage(Double overdueLoansPercentage) {
        this.overdueLoansPercentage = overdueLoansPercentage;
    }

    public Double getAverageOverdueValue() {
        return averageOverdueValue;
    }

    public void setAverageOverdueValue(Double averageOverdueValue) {
        this.averageOverdueValue = averageOverdueValue;
    }

    public Long getTotalFinesCollected() {
        return totalFinesCollected;
    }

    public void setTotalFinesCollected(Long totalFinesCollected) {
        this.totalFinesCollected = totalFinesCollected;
    }

    public Double getAverageLoanDurationDays() {
        return averageLoanDurationDays;
    }

    public void setAverageLoanDurationDays(Double averageLoanDurationDays) {
        this.averageLoanDurationDays = averageLoanDurationDays;
    }
}
