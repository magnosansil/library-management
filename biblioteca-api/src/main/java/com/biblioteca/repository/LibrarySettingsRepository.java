package com.biblioteca.repository;

import com.biblioteca.model.LibrarySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibrarySettingsRepository extends JpaRepository<LibrarySettings, Long> {
  /**
   * Busca as configurações (sempre ID 1)
   */
  Optional<LibrarySettings> findById(Long id);

  /**
   * Verifica se as configurações já existem
   */
  boolean existsById(Long id);
}
