package com.biblioteca.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Inicializador que carrega o .env ANTES de qualquer configuração do Spring
 * Boot
 * Isso garante que as variáveis estejam disponíveis quando o DataSource for
 * criado
 */
public class DotEnvInitializer implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    ConfigurableEnvironment environment = event.getEnvironment();

    try {
      Dotenv dotenv = Dotenv.configure()
          .directory("./")
          .ignoreIfMissing()
          .load();

      Map<String, Object> envMap = new HashMap<>();

      // Carrega todas as variáveis do .env
      dotenv.entries().forEach(entry -> {
        String key = entry.getKey();
        String value = entry.getValue();

        // Define como System Property
        System.setProperty(key, value);

        // Adiciona ao environment do Spring
        envMap.put(key, value);
      });

      // Adiciona as propriedades ao environment do Spring (com prioridade alta)
      environment.getPropertySources().addFirst(
          new MapPropertySource("dotenv", envMap));

      System.out.println("✅ Arquivo .env carregado! (" + envMap.size() + " variáveis)");
    } catch (Exception e) {
      System.out.println("⚠️  Arquivo .env não encontrado ou erro ao carregar: " + e.getMessage());
    }
  }
}
