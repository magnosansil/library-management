package com.biblioteca.service;

import com.biblioteca.model.LibrarySettings;
import com.biblioteca.repository.LibrarySettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o serviço de configurações da biblioteca
 * Testa leitura, atualização e valores padrão
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class LibrarySettingsServiceTest {

    @Autowired
    private LibrarySettingsService settingsService;

    @Autowired
    private LibrarySettingsRepository settingsRepository;

    @BeforeEach
    public void setUp() {
        settingsRepository.deleteAll();
    }

    // ======================== TESTES DE CONFIGURAÇÕES PADRÃO ========================

    @Test
    public void testGetSettingsReturnsDefaultValues() {
        // Act
        LibrarySettings settings = settingsService.getSettings();

        // Assert
        assertNotNull(settings);
        assertEquals(14, settings.getLoanPeriodDays());
        assertEquals(3, settings.getMaxLoansPerStudent());
        assertEquals(100, settings.getFinePerDay());
    }

    @Test
    public void testGetLoanPeriodDaysReturnsDefault() {
        // Act
        Integer loanPeriodDays = settingsService.getLoanPeriodDays();

        // Assert
        assertEquals(14, loanPeriodDays);
    }

    @Test
    public void testGetMaxLoansPerStudentReturnsDefault() {
        // Act
        Integer maxLoans = settingsService.getMaxLoansPerStudent();

        // Assert
        assertEquals(3, maxLoans);
    }

    @Test
    public void testGetFinePerDayReturnsDefault() {
        // Act
        Integer finePerDay = settingsService.getFinePerDay();

        // Assert
        assertEquals(100, finePerDay);
    }

    // ======================== TESTES DE ATUALIZAÇÃO ========================

    @Test
    public void testUpdateSettingsChangesLoanPeriod() {
        // Arrange
        LibrarySettings newSettings = new LibrarySettings();
        newSettings.setLoanPeriodDays(21);
        newSettings.setMaxLoansPerStudent(3);
        newSettings.setFinePerDay(100);

        // Act
        LibrarySettings updated = settingsService.updateSettings(newSettings);

        // Assert
        assertEquals(21, updated.getLoanPeriodDays());
        assertEquals(21, settingsService.getLoanPeriodDays());
    }

    @Test
    public void testUpdateSettingsChangesMaxLoansPerStudent() {
        // Arrange
        LibrarySettings newSettings = new LibrarySettings();
        newSettings.setLoanPeriodDays(14);
        newSettings.setMaxLoansPerStudent(5);
        newSettings.setFinePerDay(100);

        // Act
        LibrarySettings updated = settingsService.updateSettings(newSettings);

        // Assert
        assertEquals(5, updated.getMaxLoansPerStudent());
        assertEquals(5, settingsService.getMaxLoansPerStudent());
    }

    @Test
    public void testUpdateSettingsChangesFinePerDay() {
        // Arrange
        LibrarySettings newSettings = new LibrarySettings();
        newSettings.setLoanPeriodDays(14);
        newSettings.setMaxLoansPerStudent(3);
        newSettings.setFinePerDay(200);

        // Act
        LibrarySettings updated = settingsService.updateSettings(newSettings);

        // Assert
        assertEquals(200, updated.getFinePerDay());
        assertEquals(200, settingsService.getFinePerDay());
    }

    @Test
    public void testUpdateMultipleSettingsAtOnce() {
        // Arrange
        LibrarySettings newSettings = new LibrarySettings();
        newSettings.setLoanPeriodDays(21);
        newSettings.setMaxLoansPerStudent(5);
        newSettings.setFinePerDay(150);

        // Act
        LibrarySettings updated = settingsService.updateSettings(newSettings);

        // Assert
        assertEquals(21, updated.getLoanPeriodDays());
        assertEquals(5, updated.getMaxLoansPerStudent());
        assertEquals(150, updated.getFinePerDay());
    }

    @Test
    public void testSettingsIdAlwaysOne() {
        // Act
        LibrarySettings settings = settingsService.getSettings();

        // Assert
        assertEquals(1L, settings.getId());
    }

    @Test
    public void testMultipleCallsReturnSameSettings() {
        // Arrange
        LibrarySettings newSettings = new LibrarySettings();
        newSettings.setLoanPeriodDays(21);
        newSettings.setMaxLoansPerStudent(5);
        newSettings.setFinePerDay(150);
        settingsService.updateSettings(newSettings);

        // Act
        LibrarySettings first = settingsService.getSettings();
        LibrarySettings second = settingsService.getSettings();

        // Assert
        assertEquals(first.getId(), second.getId());
        assertEquals(first.getLoanPeriodDays(), second.getLoanPeriodDays());
        assertEquals(first.getMaxLoansPerStudent(), second.getMaxLoansPerStudent());
        assertEquals(first.getFinePerDay(), second.getFinePerDay());
    }
}
