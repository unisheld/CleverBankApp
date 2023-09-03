package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.interfaces.TransactionService;
import com.cleverbank.models.Account;
import lombok.AllArgsConstructor;



/**
 * Сервис для выполнения операции пополнения счета.
 */

@AllArgsConstructor
public class DepositService implements TransactionService {
    private final TransactionSaver transactionSaver;
    private final CheckGeneratorService checkGeneratorService;
    private final DatabaseUpdater databaseUpdater;

    /**
     * Выполняет операцию пополнения счета.
     *
     * @param account Счет для пополнения.
     * @param amount  Сумма для пополнения.
     */
    @Override
    public void execute(Account account, double amount) {
        if (account == null) {
            System.out.println("Ошибка: Счет для пополнения не существует.");
            return;
        }

        // Выполняем операцию пополнения счета
        account.setBalance(account.getBalance() + amount);

        // Обновляем баланс счета в базе данных с использованием DatabaseUpdater
        databaseUpdater.updateAccountBalance(account.getId(), account.getBalance());

        // После успешной операции пополнения, сохраняем информацию о транзакции
        transactionSaver.saveDepositTransaction(account, amount);

        // Генерируем чек
        checkGeneratorService.generateDepositCheck(account.getBank().getName(), account, amount);
    }
}
