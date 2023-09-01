package com.cleverbank.interfaces;

import com.cleverbank.models.Account;

/**
 * Интерфейс для сервиса выполнения транзакции.
 */
public interface TransactionService {
    /**
     * Выполняет транзакцию на указанном счете.
     *
     * @param account Счет, на котором выполняется транзакция.
     * @param amount  Сумма транзакции.
     */
    void execute(Account account, double amount);
}
