package com.biblioteca.repository;

import com.biblioteca.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    // ISBN é a chave primária, então findByIsbn não é mais necessário
    // Use findById(isbn) diretamente
}
