package com.biblioteca.repository;

import com.biblioteca.model.LibrarySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade LibrarySettings.
 *
 * <p>As configurações globais da biblioteca são armazenadas em uma única
 * entidade (espera-se id = 1). Fornece operações básicas herdadas de
 * JpaRepository e métodos auxiliares explícitos para busca/verificação por id.</p>
 */
@Repository
public interface LibrarySettingsRepository extends JpaRepository<LibrarySettings, Long> {
    /**
     * Busca as configurações pelo identificador.
     *
     * @param id identificador (esperado 1)
     * @return Optional contendo as configurações se existentes
     */
    Optional<LibrarySettings> findById(Long id);

    /**
     * Verifica se as configurações existem para o identificador informado.
     *
     * @param id identificador (esperado 1)
     * @return true se existir entrada com o id fornecido, caso contrário false
     */
    boolean existsById(Long id);
}
