package com.biblioteca.dto;

/**
 * DTO para relatório de análise de reservas
 * Fornece análise de padrões de reservas, fila média e eficácia do sistema
 */
public class ReportReservationAnalyticsDTO {
    private Long totalReservations;
    private Long activeReservations;
    private Long fulfilledReservations;
    private Long cancelledReservations;
    private Double fulfillmentRate;
    private Double averageQueuePosition;
    private Long booksWithReservations;
    private Long booksWithFullQueue;
    private Double averageWaitTimeInDays;
    private Long studentsWithReservations;

    // Constructors
    public ReportReservationAnalyticsDTO() {
    }

    public ReportReservationAnalyticsDTO(Long totalReservations, Long activeReservations,
                                         Long fulfilledReservations, Long cancelledReservations,
                                         Double fulfillmentRate, Double averageQueuePosition,
                                         Long booksWithReservations, Long booksWithFullQueue,
                                         Double averageWaitTimeInDays, Long studentsWithReservations) {
        this.totalReservations = totalReservations;
        this.activeReservations = activeReservations;
        this.fulfilledReservations = fulfilledReservations;
        this.cancelledReservations = cancelledReservations;
        this.fulfillmentRate = fulfillmentRate;
        this.averageQueuePosition = averageQueuePosition;
        this.booksWithReservations = booksWithReservations;
        this.booksWithFullQueue = booksWithFullQueue;
        this.averageWaitTimeInDays = averageWaitTimeInDays;
        this.studentsWithReservations = studentsWithReservations;
    }

    // Getters e Setters
    public Long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(Long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public Long getActiveReservations() {
        return activeReservations;
    }

    public void setActiveReservations(Long activeReservations) {
        this.activeReservations = activeReservations;
    }

    public Long getFulfilledReservations() {
        return fulfilledReservations;
    }

    public void setFulfilledReservations(Long fulfilledReservations) {
        this.fulfilledReservations = fulfilledReservations;
    }

    public Long getCancelledReservations() {
        return cancelledReservations;
    }

    public void setCancelledReservations(Long cancelledReservations) {
        this.cancelledReservations = cancelledReservations;
    }

    public Double getFulfillmentRate() {
        return fulfillmentRate;
    }

    public void setFulfillmentRate(Double fulfillmentRate) {
        this.fulfillmentRate = fulfillmentRate;
    }

    public Double getAverageQueuePosition() {
        return averageQueuePosition;
    }

    public void setAverageQueuePosition(Double averageQueuePosition) {
        this.averageQueuePosition = averageQueuePosition;
    }

    public Long getBooksWithReservations() {
        return booksWithReservations;
    }

    public void setBooksWithReservations(Long booksWithReservations) {
        this.booksWithReservations = booksWithReservations;
    }

    public Long getBooksWithFullQueue() {
        return booksWithFullQueue;
    }

    public void setBooksWithFullQueue(Long booksWithFullQueue) {
        this.booksWithFullQueue = booksWithFullQueue;
    }

    public Double getAverageWaitTimeInDays() {
        return averageWaitTimeInDays;
    }

    public void setAverageWaitTimeInDays(Double averageWaitTimeInDays) {
        this.averageWaitTimeInDays = averageWaitTimeInDays;
    }

    public Long getStudentsWithReservations() {
        return studentsWithReservations;
    }

    public void setStudentsWithReservations(Long studentsWithReservations) {
        this.studentsWithReservations = studentsWithReservations;
    }
}
