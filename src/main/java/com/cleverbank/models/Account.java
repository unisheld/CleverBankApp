package com.cleverbank.models;

import com.cleverbank.exceptions.InsufficientFundsException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Модель счета в банке.
 */
@Data
@AllArgsConstructor
public class Account {
    private long id;
    private double balance;
    private Client client;
    private Bank bank;
    private LocalDateTime dateOpen;

    /**
     * Внесение средств на счет.
     *
     * @param amount Сумма для внесения.
     */
    public synchronized void deposit(double amount) {
        balance += amount;
    }

    /**
     * Снятие средств со счета.
     *
     * @param amount Сумма для снятия.
     * @throws InsufficientFundsException Если на счете недостаточно средств.
     */
    public synchronized void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        } else {
            throw new InsufficientFundsException("Недостаточно средств на счете.");
        }
    }
}
