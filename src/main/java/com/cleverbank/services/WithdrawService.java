package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.exceptions.InsufficientFundsException;
import com.cleverbank.interfaces.TransactionService;
import com.cleverbank.models.Account;
import lombok.AllArgsConstructor;

/**
 * Сервис для выполнения операции снятия средств со счета.
 */
@AllArgsConstructor
public class WithdrawService implements TransactionService {
    private final TransactionSaver transactionSaver;
    private final CheckGeneratorService checkGeneratorService;
    private final DatabaseUpdater databaseUpdater;

    /**
     * Выполняет операцию снятия средств со счета.
     *
     * @param account Счет, с которого осуществляется снятие.
     * @param amount  Сумма для снятия.
     * @throws InsufficientFundsException если на счете недостаточно средств.
     */
    @Override
    public void execute(Account account, double amount) {
        if (account == null) {
            System.out.println("Ошибка: Счет для снятия не существует.");
            return;
        }

        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);

            // Обновляем баланс счета в базе данных
            databaseUpdater.updateAccountBalance(account.getId(), account.getBalance());

            // Сохраняем информацию о транзакции
            transactionSaver.saveWithdrawTransaction(account, amount);

            // Генерируем чек
            checkGeneratorService.generateWithdrawCheck(account.getBank().getName(), account, amount);
        } else {
            throw new InsufficientFundsException("Недостаточно средств на счете.");
        }
    }
}
