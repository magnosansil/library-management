package com.biblioteca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

  @Autowired
  private DataSource dataSource;

  @GetMapping
  public ResponseEntity<Map<String, Object>> health() {
    Map<String, Object> health = new HashMap<>();
    health.put("status", "UP");
    health.put("timestamp", System.currentTimeMillis());

    // Verificar conexão com banco de dados
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();
      Map<String, Object> db = new HashMap<>();
      db.put("status", "CONNECTED");
      db.put("database", metaData.getDatabaseProductName());
      db.put("version", metaData.getDatabaseProductVersion());
      db.put("url", metaData.getURL());
      db.put("driver", metaData.getDriverName());
      health.put("database", db);
    } catch (Exception e) {
      Map<String, Object> db = new HashMap<>();
      db.put("status", "DISCONNECTED");
      db.put("error", e.getMessage());
      health.put("database", db);
      health.put("status", "DOWN");
    }

    return ResponseEntity.ok(health);
  }
}
