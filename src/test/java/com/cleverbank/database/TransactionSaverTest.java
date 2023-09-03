package com.cleverbank.database;

import com.cleverbank.database.TransactionDAO;
import com.cleverbank.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class TransactionSaverTest {

    @Mock
    private TransactionDAO transactionDAO;

    private TransactionSaver transactionSaver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализируем моки

        transactionSaver = new TransactionSaver(transactionDAO);
    }

    @Test
    void testSaveDepositTransaction_Success() throws SQLException {
        Client client1 = new Client(1, "client 1");
        Bank bank1 = new Bank(1, "bankA");
        Account account = new Account(1, 100.0, client1, bank1, LocalDateTime.now());

        double amount = 100.0;

        // Задаем поведение мока transactionDAO.save
        doNothing().when(transactionDAO).save(any());

        // Вызываем метод, который мы тестируем
        transactionSaver.saveDepositTransaction(account, amount);

        // Проверяем, что метод transactionDAO.save был вызван с правильными аргументами
        verify(transactionDAO).save(argThat(transaction ->
                transaction.getSenderAccountId() == account.getId() &&
                        transaction.getReceiverAccountId() == account.getId() &&
                        transaction.getAmount() == amount &&
                        transaction.getTransactionType().equals(TransactionType.DEPOSIT.name())
        ));
    }

    @Test
    void testSaveWithdrawTransaction_Success() throws SQLException {
        Client client1 = new Client(1, "client 1");
        Bank bank1 = new Bank(1, "bankA");
        Account account = new Account(1, 100.0, client1, bank1, LocalDateTime.now());

        double amount = 50.0;

        // Задаем поведение мока transactionDAO.save
        doNothing().when(transactionDAO).save(any());

        // Вызываем метод, который мы тестируем
        transactionSaver.saveWithdrawTransaction(account, amount);

        // Проверяем, что метод transactionDAO.save был вызван с правильными аргументами
        verify(transactionDAO).save(argThat(transaction ->
                transaction.getSenderAccountId() == account.getId() &&
                        transaction.getReceiverAccountId() == account.getId() &&
                        transaction.getAmount() == amount &&
                        transaction.getTransactionType().equals(TransactionType.WITHDRAW.name())
        ));
    }

    @Test
    void testSaveTransferTransaction_Success() throws SQLException {
        Client client1 = new Client(1, "client 1");
        Client client2 = new Client(2, "client 2");
        Bank bank1 = new Bank(1, "bankA");
        Account sender = new Account(1, 100.0, client1, bank1, LocalDateTime.now());
        Account receiver = new Account(2, 500.0, client2, bank1, LocalDateTime.now());
        double amount = 200.0;

        // Задаем поведение мока transactionDAO.save
        doNothing().when(transactionDAO).save(any());

        // Вызываем метод, который мы тестируем
        transactionSaver.saveTransferTransaction(sender, receiver, amount);

        // Проверяем, что метод transactionDAO.save был вызван с правильными аргументами
        verify(transactionDAO).save(argThat(transaction ->
                transaction.getSenderAccountId() == sender.getId() &&
                        transaction.getReceiverAccountId() == receiver.getId() &&
                        transaction.getAmount() == amount &&
                        transaction.getTransactionType().equals(TransactionType.TRANSFER.name())
        ));
    }
}

