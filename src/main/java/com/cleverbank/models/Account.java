package com.cleverbank.models;

import com.cleverbank.exceptions.InsufficientFundsException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс, представляющий счет.
 */
@Data
@AllArgsConstructor
public class Account {
    private int id;
    private double balance;
    private Client client;
    private Bank bank;
    private LocalDateTime dateOpen;

    /**
     * Конструктор для создания объекта Account с указанными параметрами.
     *
     * @param id       Идентификатор счета.
     * @param balance  Баланс счета.
     * @param clientId Идентификатор клиента.
     * @param clientName Имя клиента.
     * @param bankId   Идентификатор банка.
     * @param bankName Имя банка.
     * @param dateOpen Дата открытия счета.
     */
    public Account(int id, double balance, long clientId, String clientName, long bankId, String bankName, LocalDateTime dateOpen) {
        this.id = id;
        this.balance = balance;
        this.client = new Client(clientId, clientName);
        this.bank = new Bank(bankId, bankName);
        this.dateOpen = dateOpen;
    }

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
