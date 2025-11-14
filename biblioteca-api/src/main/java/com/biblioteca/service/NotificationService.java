package com.biblioteca.service;

import com.biblioteca.model.Loan;
import com.biblioteca.data.structures.LinkedList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de notificações que utiliza a estrutura de dados LinkedList
 * para gerenciar uma fila de notificações de empréstimos em atraso.
 */
@Service
public class NotificationService {
    private final LinkedList<Loan> overdueNotifications;

    public NotificationService() {
        // Usando a estrutura de dados LinkedList do repositório
        this.overdueNotifications = new LinkedList<>(100);
    }

    /**
     * Adiciona um empréstimo em atraso à fila de notificações
     */
    public void addOverdueNotification(Loan loan) {
        if (!overdueNotifications.isFull()) {
            overdueNotifications.append(loan);
        }
    }

    /**
     * Processa todas as notificações pendentes e retorna a lista
     */
    public List<Loan> processNotifications() {
        List<Loan> notifications = new ArrayList<>();
        
        while (!overdueNotifications.isEmpty()) {
            Loan loan = (Loan) overdueNotifications.delete(0);
            notifications.add(loan);
        }
        
        return notifications;
    }

    /**
     * Retorna todas as notificações sem removê-las
     */
    public List<Loan> getAllNotifications() {
        List<Loan> notifications = new ArrayList<>();
        Object[] loans = overdueNotifications.selectAll();
        
        for (Object loan : loans) {
            notifications.add((Loan) loan);
        }
        
        return notifications;
    }

    /**
     * Limpa todas as notificações
     */
    public void clearNotifications() {
        overdueNotifications.clear();
    }

    /**
     * Verifica se há notificações pendentes
     */
    public boolean hasNotifications() {
        return !overdueNotifications.isEmpty();
    }
}

