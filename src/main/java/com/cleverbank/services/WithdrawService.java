package com.cleverbank.services;

import com.cleverbank.database.TransactionSaver;
import com.cleverbank.exceptions.InsufficientFundsException;
import com.cleverbank.interfaces.TransactionService;
import com.cleverbank.models.Account;
import lombok.Data;

/**
 * Сервис для выполнения операции снятия средств со счета.
 */
@Data
public class WithdrawService implements TransactionService {
    private final TransactionSaver transactionSave;
    private final CheckGeneratorService checkGeneratorService;

    /**
     * Выполняет операцию снятия средств со счета.
     *
     * @param account Счет, с которого осуществляется снятие.
     * @param amount  Сумма для снятия.
     * @throws InsufficientFundsException если на счете недостаточно средств.
     */
    @Override
    public void execute(Account account, double amount) {
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);

            // Сохраняем информацию о транзакции
            transactionSave.saveWithdrawTransaction(account, amount);
            checkGeneratorService.generateWithdrawCheck(account.getBank().getName(), account, amount);
        } else {
            throw new InsufficientFundsException("Недостаточно средств на счете.");
        }
    }
}
