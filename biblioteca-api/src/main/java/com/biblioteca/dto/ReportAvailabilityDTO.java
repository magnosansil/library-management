package com.biblioteca.dto;

/**
 * DTO para relatório de disponibilidade do acervo
 * Agrupa livros por estado: disponíveis, indisponíveis e parcialmente disponíveis
 */
public class ReportAvailabilityDTO {
    private Long totalBooks;
    private Long availableBooks;
    private Long unavailableBooks;
    private Double availabilityPercentage;
    private Long totalCopiesInStock;
    private Long totalCopiesAvailable;

    // Constructors
    public ReportAvailabilityDTO() {
    }

    public ReportAvailabilityDTO(Long totalBooks, Long availableBooks, Long unavailableBooks,
                                  Double availabilityPercentage, Long totalCopiesInStock, 
                                  Long totalCopiesAvailable) {
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.unavailableBooks = unavailableBooks;
        this.availabilityPercentage = availabilityPercentage;
        this.totalCopiesInStock = totalCopiesInStock;
        this.totalCopiesAvailable = totalCopiesAvailable;
    }

    // Getters e Setters
    public Long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(Long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public Long getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(Long availableBooks) {
        this.availableBooks = availableBooks;
    }

    public Long getUnavailableBooks() {
        return unavailableBooks;
    }

    public void setUnavailableBooks(Long unavailableBooks) {
        this.unavailableBooks = unavailableBooks;
    }

    public Double getAvailabilityPercentage() {
        return availabilityPercentage;
    }

    public void setAvailabilityPercentage(Double availabilityPercentage) {
        this.availabilityPercentage = availabilityPercentage;
    }

    public Long getTotalCopiesInStock() {
        return totalCopiesInStock;
    }

    public void setTotalCopiesInStock(Long totalCopiesInStock) {
        this.totalCopiesInStock = totalCopiesInStock;
    }

    public Long getTotalCopiesAvailable() {
        return totalCopiesAvailable;
    }

    public void setTotalCopiesAvailable(Long totalCopiesAvailable) {
        this.totalCopiesAvailable = totalCopiesAvailable;
    }
}
