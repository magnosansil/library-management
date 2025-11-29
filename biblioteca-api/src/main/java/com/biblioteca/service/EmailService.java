package com.biblioteca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo envio de e-mails e notificações do sistema.
 * Centraliza a lógica de comunicação com os usuários,
 * incluindo avisos de atraso, confirmações de reserva e outras mensagens automatizadas.
 */

@Service
public class EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${spring.mail.properties.mail.from-name:Biblioteca}")
  private String fromName;

  @Autowired
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Envia e-mail de notificação de livro em atraso
   */
  public void sendOverdueLoanNotification(String toEmail, String studentName, String bookTitle, String bookIsbn,
      Integer overdueDays) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(toEmail);
      message.setSubject("📚 Aviso: Livro em Atraso - " + bookTitle);

      String body = String.format(
          "Olá %s,\n\n" +
              "Este é um aviso automático da Biblioteca.\n\n" +
              "Você possui o livro \"%s\" (ISBN: %s) em atraso.\n" +
              "Dias de atraso: %d\n\n" +
              "Por favor, devolva o livro o quanto antes para evitar multas adicionais.\n\n" +
              "Atenciosamente,\n" +
              "Sistema de Biblioteca",
          studentName, bookTitle, bookIsbn, overdueDays);

      message.setText(body);
      mailSender.send(message);
      logger.info("✅ E-mail de atraso enviado para: {}", toEmail);
    } catch (Exception e) {
      logger.error("❌ Erro ao enviar e-mail de atraso para {}: {}", toEmail, e.getMessage());
      throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
    }
  }

  /**
   * Envia e-mail de notificação de livro reservado disponível
   */
  public void sendReservationAvailableNotification(String toEmail, String studentName, String bookTitle,
      String bookIsbn) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(toEmail);
      message.setSubject("📚 Livro Reservado Disponível - " + bookTitle);

      String body = String.format(
          "Olá %s,\n\n" +
              "Boa notícia! O livro que você reservou está disponível.\n\n" +
              "Livro: \"%s\"\n" +
              "ISBN: %s\n\n" +
              "Você tem prioridade para retirar este livro. " +
              "Entre em contato com a biblioteca ou acesse o sistema para efetivar o empréstimo.\n\n" +
              "Atenciosamente,\n" +
              "Sistema de Biblioteca",
          studentName, bookTitle, bookIsbn);

      message.setText(body);
      mailSender.send(message);
      logger.info("✅ E-mail de reserva disponível enviado para: {}", toEmail);
    } catch (Exception e) {
      logger.error("❌ Erro ao enviar e-mail de reserva disponível para {}: {}", toEmail, e.getMessage());
      throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
    }
  }
}
