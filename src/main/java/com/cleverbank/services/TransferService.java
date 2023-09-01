package com.cleverbank.services;

import com.cleverbank.database.TransactionSaver;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;

import com.cleverbank.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Сервис для выполнения операции перевода средств между счетами.
 */

@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TransferService implements com.cleverbank.interfaces.TransferService {
    private final TransactionSaver transactionSave;
    private final CheckGeneratorService checkGeneratorService;

    /**
     * Выполняет операцию перевода средств между счетами.
     *
     * @param sender   Счет отправителя.
     * @param receiver Счет получателя.
     * @param amount   Сумма для перевода.
     * @return true, если операция перевода выполнена успешно, в противном случае - false.
     */
    public boolean transfer(Account sender, Account receiver, double amount) {
        Bank senderBank = sender.getBank();
        Bank receiverBank = receiver.getBank();

        if (senderBank.equals(receiverBank)) {
            // Обычный перевод между счетами в одном банке
            synchronized (sender) {
                synchronized (receiver) {
                    if (sender.getBalance() >= amount) {
                        sender.withdraw(amount);
                        receiver.deposit(amount);

                        // Здесь сохраняем информацию о транзакции
                        transactionSave.saveTransferTransaction(sender, receiver, amount);
                        checkGeneratorService.generateTransferCheck(TransactionType.TRANSFER, senderBank.getName(), receiverBank.getName(), sender, receiver, amount);

                        return true;
                    }
                }
            }
        } else {
            // Перевод между счетами в разных банках
            Bank firstBank = senderBank.getId() < receiverBank.getId() ? senderBank : receiverBank;
            Bank secondBank = senderBank.getId() < receiverBank.getId() ? receiverBank : senderBank;

            synchronized (firstBank) {
                synchronized (secondBank) {
                    synchronized (sender) {
                        synchronized (receiver) {
                            if (sender.getBalance() >= amount) {
                                sender.withdraw(amount);
                                receiver.deposit(amount);

                                // Здесь также сохраняем информацию о транзакции
                                transactionSave.saveTransferTransaction(sender, receiver, amount);
                                checkGeneratorService.generateTransferCheck(TransactionType.TRANSFER, senderBank.getName(), receiverBank.getName(), sender, receiver, amount);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
