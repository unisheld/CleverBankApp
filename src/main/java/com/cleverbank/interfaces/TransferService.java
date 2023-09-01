package com.cleverbank.interfaces;

import com.cleverbank.models.Account;

/**
 * Интерфейс для сервиса перевода средств между счетами.
 */
public interface TransferService {
    /**
     * Переводит указанную сумму с одного счета на другой.
     *
     * @param sender   Счет отправителя.
     * @param receiver Счет получателя.
     * @param amount   Сумма перевода.
     * @return true, если перевод выполнен успешно, иначе false.
     */
    boolean transfer(Account sender, Account receiver, double amount);
}
