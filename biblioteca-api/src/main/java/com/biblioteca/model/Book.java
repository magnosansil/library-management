package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
  @Id
  @NotBlank(message = "ISBN é obrigatório")
  @Column(nullable = false, unique = true, length = 50)
  private String isbn;

  @NotBlank(message = "Título é obrigatório")
  @Column(nullable = false)
  private String title;

  @NotBlank(message = "Autor é obrigatório")
  @Column(nullable = false)
  private String author;

  @Column(name = "cover_image_url", length = 500)
  private String coverImageUrl;

  @Column(name = "keywords", length = 500)
  private String keywords;

  @Column(name = "synopsis", columnDefinition = "TEXT")
  private String synopsis;

  @Column(name = "entry_date", nullable = false, updatable = false)
  private LocalDate entryDate;

  @NotNull(message = "Quantidade é obrigatória")
  @Column(nullable = false)
  private Integer quantity = 0;

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Reservation> reservations = new ArrayList<>();

  /**
   * Quantidade de reservas ativas para este livro
   * Atualizado automaticamente pelo sistema
   */
  @Column(name = "active_reservations_count", nullable = false)
  private Integer activeReservationsCount = 0;

  // Constructors
  public Book() {
  }

  public Book(String isbn, String title, String author, String coverImageUrl, String keywords,
              String synopsis, LocalDate entryDate, Integer quantity, Integer activeReservationsCount) {
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.coverImageUrl = coverImageUrl;
    this.keywords = keywords;
    this.synopsis = synopsis;
    this.entryDate = entryDate;
    this.quantity = quantity;
    this.activeReservationsCount = activeReservationsCount;
  }

  // Getters and Setters
  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public LocalDate getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDate entryDate) {
    this.entryDate = entryDate;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public List<Reservation> getReservations() {
    return reservations;
  }

  public void setReservations(List<Reservation> reservations) {
    this.reservations = reservations;
  }

  public Integer getActiveReservationsCount() {
    return activeReservationsCount;
  }

  public void setActiveReservationsCount(Integer activeReservationsCount) {
    this.activeReservationsCount = activeReservationsCount;
  }

  @PrePersist
  protected void onCreate() {
    if (entryDate == null) {
      entryDate = LocalDate.now();
    }
  }
}
