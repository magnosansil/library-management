package com.biblioteca.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * Componente que executa migrações SQL automaticamente na inicialização
 * Executa scripts de migração encontrados em src/main/resources/
 */
@Component
public class DatabaseMigrationRunner {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @PostConstruct
  public void runMigrations() {
    logger.info("🔄 Iniciando execução de migrações SQL...");

    // Lista de migrações a serem executadas (em ordem)
    String[] migrations = {
        "migration-add-fine-system.sql",
        "migration-add-reservations.sql",
        "migration-add-student-contact.sql"
    };

    for (String migrationFile : migrations) {
      try {
        executeMigration(migrationFile);
      } catch (Exception e) {
        logger.warn("⚠️  Aviso ao executar migração {}: {}", migrationFile, e.getMessage());
      }
    }

    logger.info("✅ Execução de migrações concluída!");
  }

  private void executeMigration(String migrationFile) {
    try {
      logger.info("📄 Executando migração: {}", migrationFile);

      // Carregar arquivo SQL do classpath
      ClassPathResource resource = new ClassPathResource(migrationFile);

      if (!resource.exists()) {
        logger.warn("⚠️  Arquivo de migração não encontrado: {}", migrationFile);
        return;
      }

      // Ler conteúdo do arquivo
      String sql = StreamUtils.copyToString(
          resource.getInputStream(),
          StandardCharsets.UTF_8);

      // Executar SQL
      jdbcTemplate.execute(sql);

      logger.info("✅ Migração {} executada com sucesso!", migrationFile);

    } catch (Exception e) {
      // Os scripts SQL têm verificações IF NOT EXISTS, então erros de "já existe" são
      // esperados
      String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

      if (errorMsg.contains("já existe") ||
          errorMsg.contains("already exists") ||
          errorMsg.contains("duplicate") ||
          (errorMsg.contains("relation") && errorMsg.contains("already exists"))) {
        logger.info("ℹ️  Migração {} já foi executada anteriormente (isso é normal)", migrationFile);
      } else {
        // Para outros erros, apenas loga como warning
        logger.warn("⚠️  Aviso ao executar migração {}: {}", migrationFile, e.getMessage());
      }
    }
  }
}
