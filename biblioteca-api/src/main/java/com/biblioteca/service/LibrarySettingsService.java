package com.biblioteca.service;

import com.biblioteca.model.LibrarySettings;
import com.biblioteca.repository.LibrarySettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço para gerenciar configurações globais da biblioteca
 */
@Service
public class LibrarySettingsService {

  private final LibrarySettingsRepository settingsRepository;
  private static final Long SETTINGS_ID = 1L;

  @Autowired
  public LibrarySettingsService(LibrarySettingsRepository settingsRepository) {
    this.settingsRepository = settingsRepository;
    // Garantir que as configurações padrão existam
    initializeDefaultSettings();
  }

  /**
   * Inicializa as configurações padrão se não existirem
   */
  @Transactional
  private void initializeDefaultSettings() {
    if (!settingsRepository.existsById(SETTINGS_ID)) {
      LibrarySettings defaultSettings = new LibrarySettings();
      defaultSettings.setId(SETTINGS_ID);
      defaultSettings.setLoanPeriodDays(14);
      defaultSettings.setMaxLoansPerStudent(3);
      defaultSettings.setFinePerDay(100);
      settingsRepository.save(defaultSettings);
    }
  }

  /**
   * Obtém as configurações atuais
   */
  public LibrarySettings getSettings() {
    return settingsRepository.findById(SETTINGS_ID)
        .orElseGet(() -> {
          // Se não existir, criar com valores padrão
          LibrarySettings defaultSettings = new LibrarySettings();
          defaultSettings.setId(SETTINGS_ID);
          defaultSettings.setLoanPeriodDays(14);
          defaultSettings.setMaxLoansPerStudent(3);
          defaultSettings.setFinePerDay(100);
          return settingsRepository.save(defaultSettings);
        });
  }

  /**
   * Atualiza as configurações
   */
  @Transactional
  public LibrarySettings updateSettings(LibrarySettings newSettings) {
    LibrarySettings currentSettings = getSettings();
    currentSettings.setLoanPeriodDays(newSettings.getLoanPeriodDays());
    currentSettings.setMaxLoansPerStudent(newSettings.getMaxLoansPerStudent());
    currentSettings.setFinePerDay(newSettings.getFinePerDay());
    return settingsRepository.save(currentSettings);
  }

  /**
   * Obtém o prazo de devolução em dias
   */
  public Integer getLoanPeriodDays() {
    return getSettings().getLoanPeriodDays();
  }

  /**
   * Obtém o limite máximo de empréstimos por aluno
   */
  public Integer getMaxLoansPerStudent() {
    return getSettings().getMaxLoansPerStudent();
  }

  /**
   * Obtém a multa por dia de atraso
   */
  public Integer getFinePerDay() {
    return getSettings().getFinePerDay();
  }
}
