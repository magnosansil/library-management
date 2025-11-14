package com.biblioteca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class IndexController {

  @GetMapping
  public ResponseEntity<Map<String, Object>> index() {
    Map<String, Object> info = new HashMap<>();
    info.put("name", "Biblioteca API");
    info.put("version", "1.0.0");
    info.put("status", "running");
    info.put("description", "API de Gerenciamento de Biblioteca com Estruturas de Dados");

    Map<String, String> endpoints = new HashMap<>();
    endpoints.put("health", "/api/health");
    endpoints.put("routes", "/api/routes");
    endpoints.put("settings", "/api/settings");
    endpoints.put("students", "/api/students");
    endpoints.put("books", "/api/books");
    endpoints.put("loans", "/api/loans");
    endpoints.put("active_loans", "/api/loans/active");

    info.put("endpoints", endpoints);
    info.put("documentation", "Veja ROUTES.md para todas as rotas centralizadas");
    info.put("routes_file", "RoutesController.java - Arquivo centralizado com todas as rotas");

    return ResponseEntity.ok(info);
  }
}
