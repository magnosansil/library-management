package com.biblioteca;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Biblioteca.
 *
 * <p>Responsável por inicializar o Spring Boot e carregar variáveis de ambiente
 * a partir do arquivo <code>.env</code> antes da execução da aplicação.</p>
 *
 * <p>O carregamento antecipado das variáveis garante que configurações como
 * credenciais de banco de dados e URLs sejam disponibilizadas corretamente
 * durante o ciclo de inicialização do Spring.</p>
 *
 * <p>Em caso de erro no carregamento do arquivo <code>.env</code>, a aplicação
 * continua sendo executada utilizando as variáveis de ambiente do sistema.</p>
 */
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
