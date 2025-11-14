package com.biblioteca;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BibliotecaApplication {
  public static void main(String[] args) {
    // Carregar .env ANTES de iniciar o Spring Boot
    try {
      Dotenv dotenv = Dotenv.configure()
          .directory("./")
          .ignoreIfMissing()
          .systemProperties()
          .load();

      System.out.println("✅ Arquivo .env carregado! (" + dotenv.entries().size() + " variáveis)");
      System.out.println("   DATABASE_URL configurado: " + (dotenv.get("DATABASE_URL") != null ? "Sim" : "Não"));
    } catch (Exception e) {
      System.out.println("⚠️  Erro ao carregar .env: " + e.getMessage());
      System.out.println("   Usando variáveis de ambiente do sistema.");
    }

    SpringApplication.run(BibliotecaApplication.class, args);
  }
}
