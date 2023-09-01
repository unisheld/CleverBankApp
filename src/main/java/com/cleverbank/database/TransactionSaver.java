package com.cleverbank.database;

import com.cleverbank.models.Account;
import com.cleverbank.models.Transaction;
import com.cleverbank.models.TransactionType;
import lombok.Data;


import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Класс для сохранения транзакций в базу данных.
 */
@Data
public class TransactionSaver {
    private final TransactionDAO transactionDAO;



    /**
     * Сохраняет транзакцию типа "Пополнение" в базе данных.
     *
     * @param account Счет для пополнения.
     * @param amount  Сумма пополнения.
     */
    public void saveDepositTransaction(Account account, double amount) {
        saveTransaction(account, null, amount, TransactionType.DEPOSIT);
    }

    /**
     * Сохраняет транзакцию типа "Снятие" в базе данных.
     *
     * @param account Счет для снятия средств.
     * @param amount  Сумма снятия.
     */
    public void saveWithdrawTransaction(Account account, double amount) {
        saveTransaction(account, null, amount, TransactionType.WITHDRAW);
    }

    /**
     * Сохраняет транзакцию типа "Перевод" в базе данных.
     *
     * @param sender   Счет отправителя.
     * @param receiver Счет получателя.
     * @param amount   Сумма перевода.
     */
    public void saveTransferTransaction(Account sender, Account receiver, double amount) {
        saveTransaction(sender, receiver, amount, TransactionType.TRANSFER);
    }

    private void saveTransaction(Account sender, Account receiver, double amount, TransactionType transactionType) {
        try {
            Transaction transaction = Transaction.builder()
                    .senderAccountId(sender != null ? sender.getId() : null)
                    .receiverAccountId(receiver != null ? receiver.getId() : null)
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .transactionType(transactionType.name())
                    .build();

            transactionDAO.save(transaction);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении транзакции", e);
        }
    }
}
