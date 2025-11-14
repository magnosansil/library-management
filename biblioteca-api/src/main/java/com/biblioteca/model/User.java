package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Nome é obrigatório")
  @Column(nullable = false)
  private String name;

  @Email(message = "Email deve ser válido")
  @NotBlank(message = "Email é obrigatório")
  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "max_loans", nullable = false)
  private Integer maxLoans = 3;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}

