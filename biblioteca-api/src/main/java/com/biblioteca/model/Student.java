package com.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {
  @Id
  @NotBlank(message = "Matrícula é obrigatória")
  @Column(nullable = false, unique = true, length = 50)
  private String matricula;

  @NotBlank(message = "Nome é obrigatório")
  @Column(nullable = false)
  private String nome;

  @NotBlank(message = "CPF é obrigatório")
  @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
  @Size(min = 11, max = 11, message = "CPF deve conter exatamente 11 dígitos")
  @Column(nullable = false, length = 11, unique = true)
  private String cpf;

  @Column(name = "data_nascimento", nullable = false)
  private LocalDate dataNascimento;

  @NotBlank(message = "E-mail é obrigatório")
  @Email(message = "E-mail deve ter um formato válido")
  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = true)
  private String telefone;

  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore // Evita recursão infinita ao serializar JSON
  private List<Loan> loans = new ArrayList<>();

  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore // Evita recursão infinita ao serializar JSON
  private List<Reservation> reservations = new ArrayList<>();

  /**
   * Contador de reservas registradas pelo estudante
   * Atualizado automaticamente pelo sistema
   */
  @Column(name = "reservations_count", nullable = false)
  private Integer reservationsCount = 0;

  // Constructors
  public Student() {
  }

  public Student(String matricula, String nome, String cpf, LocalDate dataNascimento, String email,
                 String telefone, Integer reservationsCount) {
    this.matricula = matricula;
    this.nome = nome;
    this.cpf = cpf;
    this.dataNascimento = dataNascimento;
    this.email = email;
    this.telefone = telefone;
    this.reservationsCount = reservationsCount;
  }

  // Getters and Setters
  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public LocalDate getDataNascimento() {
    return dataNascimento;
  }

  public void setDataNascimento(LocalDate dataNascimento) {
    this.dataNascimento = dataNascimento;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public List<Loan> getLoans() {
    return loans;
  }

  public void setLoans(List<Loan> loans) {
    this.loans = loans;
  }

  public List<Reservation> getReservations() {
    return reservations;
  }

  public void setReservations(List<Reservation> reservations) {
    this.reservations = reservations;
  }

  public Integer getReservationsCount() {
    return reservationsCount;
  }

  public void setReservationsCount(Integer reservationsCount) {
    this.reservationsCount = reservationsCount;
  }
}
