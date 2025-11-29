package com.biblioteca.dto;
/**
 * DTO que representa as informações de disponibilidade de um livro.
 * Contém campos relevantes para exibir se o livro está disponível,
 * quantidade em estoque e metadados básicos (título, autor, ISBN).
 *
 * Usado para transferência de dados entre camadas (service -> controller).
 */

public class BookAvailabilityDTO {
  private String bookIsbn;
  private String bookTitle;
  private String bookAuthor;
  private Integer quantity;
  private Boolean isAvailable;

  // Constructors
  public BookAvailabilityDTO() {
  }

  public BookAvailabilityDTO(String bookIsbn, String bookTitle, String bookAuthor, Integer quantity, Boolean isAvailable) {
    this.bookIsbn = bookIsbn;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.quantity = quantity;
    this.isAvailable = isAvailable;
  }

  // Getters and Setters
  public String getBookIsbn() {
    return bookIsbn;
  }

  public void setBookIsbn(String bookIsbn) {
    this.bookIsbn = bookIsbn;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }

  public String getBookAuthor() {
    return bookAuthor;
  }

  public void setBookAuthor(String bookAuthor) {
    this.bookAuthor = bookAuthor;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Boolean getIsAvailable() {
    return isAvailable;
  }

  public void setIsAvailable(Boolean isAvailable) {
    this.isAvailable = isAvailable;
  }
}
