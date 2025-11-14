package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

  @PrePersist
  protected void onCreate() {
    if (entryDate == null) {
      entryDate = LocalDate.now();
    }
  }
}
