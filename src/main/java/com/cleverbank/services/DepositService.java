package com.cleverbank.services;

import com.cleverbank.database.TransactionSaver;
import com.cleverbank.interfaces.TransactionService;
import com.cleverbank.models.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Сервис для выполнения операции пополнения счета.
 */
@Data
@AllArgsConstructor
public class DepositService implements TransactionService {
    private final TransactionSaver transactionSaver;
    private final CheckGeneratorService checkGeneratorService;

    /**
     * Выполняет операцию пополнения счета и сохраняет информацию о транзакции.
     *
     * @param account Счет, на который выполняется пополнение.
     * @param amount  Сумма пополнения.
     */
    @Override
    public void execute(Account account, double amount) {
        // Выполняем операцию пополнения счета
        account.setBalance(account.getBalance() + amount);

        // После успешной операции пополнения, сохраняем информацию о транзакции в базе данных
        transactionSaver.saveDepositTransaction(account, amount);

        // Генерируем чек
        checkGeneratorService.generateDepositCheck(account.getBank().getName(), account, amount);
    }
}
