package com.biblioteca.controller;

import com.biblioteca.model.LibrarySettings;
import com.biblioteca.service.LibrarySettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class LibrarySettingsController {

  private final LibrarySettingsService settingsService;

  @Autowired
  public LibrarySettingsController(LibrarySettingsService settingsService) {
    this.settingsService = settingsService;
  }

  /**
   * Obter configurações atuais
   * GET /api/settings
   */
  @GetMapping
  public ResponseEntity<LibrarySettings> getSettings() {
    LibrarySettings settings = settingsService.getSettings();
    return ResponseEntity.ok(settings);
  }

  /**
   * Atualizar configurações
   * PUT /api/settings
   */
  @PutMapping
  public ResponseEntity<LibrarySettings> updateSettings(@Valid @RequestBody LibrarySettings settings) {
    LibrarySettings updatedSettings = settingsService.updateSettings(settings);
    return ResponseEntity.ok(updatedSettings);
  }
}
