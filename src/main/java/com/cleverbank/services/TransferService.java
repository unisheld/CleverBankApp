package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.exceptions.InsufficientFundsException;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Сервис для выполнения операции перевода средств между счетами.
 */
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TransferService implements com.cleverbank.interfaces.TransferService {
    private final TransactionSaver transactionSaver;
    private final CheckGeneratorService checkGeneratorService;
    private final DatabaseUpdater databaseUpdater;

    /**
     * Выполняет операцию перевода средств между счетами.
     *
     * @param sender   Счет отправителя.
     * @param receiver Счет получателя.
     * @param amount   Сумма для перевода.
     * @return true, если операция перевода выполнена успешно, в противном случае - false.
     * @throws InsufficientFundsException если недостаточно средств на счете отправителя.
     */
    public boolean transfer(Account sender, Account receiver, double amount) {
        if (sender == null || receiver == null) {
            System.out.println("Ошибка: Один из счетов не существует.");
            return false;
        }

        // Перевод между счетами в одном банке
        if (sender.getBank().equals(receiver.getBank())) {
            synchronized (sender) {
                synchronized (receiver) {
                    if (sender.getBalance() >= amount) {
                        sender.withdraw(amount);
                        receiver.deposit(amount);

                        // Обновляем балансы обоих счетов в базе данных
                        databaseUpdater.updateAccountBalance2(sender.getId(), sender.getBalance(), receiver.getId(), receiver.getBalance());

                        // Сохраняем информацию о транзакции
                        transactionSaver.saveTransferTransaction(sender, receiver, amount);

                        // Генерируем чек
                        checkGeneratorService.generateTransferCheck(TransactionType.TRANSFER, sender.getBank().getName(), receiver.getBank().getName(), sender, receiver, amount);

                        return true;
                    } else {
                        throw new InsufficientFundsException("Ошибка: Недостаточно средств на счете отправителя.");
                    }
                }
            }
        } else {
            // Перевод между счетами в разных банках
            Bank firstBank = sender.getBank().getId() < receiver.getBank().getId() ? sender.getBank() : receiver.getBank();
            Bank secondBank = sender.getBank().getId() < receiver.getBank().getId() ? receiver.getBank() : sender.getBank();

            synchronized (firstBank) {
                synchronized (secondBank) {
                    synchronized (sender) {
                        synchronized (receiver) {
                            if (sender.getBalance() >= amount) {
                                sender.withdraw(amount);
                                receiver.deposit(amount);

                                // Обновляем балансы обоих счетов в базе данных
                                databaseUpdater.updateAccountBalance2(sender.getId(), sender.getBalance(), receiver.getId(), receiver.getBalance());

                                // Сохраняем информацию о транзакции
                                transactionSaver.saveTransferTransaction(sender, receiver, amount);

                                // Генерируем чек
                                checkGeneratorService.generateTransferCheck(TransactionType.TRANSFER, sender.getBank().getName(), receiver.getBank().getName(), sender, receiver, amount);

                                return true;
                            } else {
                                throw new InsufficientFundsException("Ошибка: Недостаточно средств на счете отправителя.");
                            }
                        }
                    }
                }
            }
        }
    }
}
