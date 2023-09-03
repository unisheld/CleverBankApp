package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.exceptions.InsufficientFundsException;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.Client;
import com.cleverbank.models.TransactionType;
import com.cleverbank.services.CheckGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferServiceTest {

    @Mock
    private TransactionSaver transactionSaver;

    @Mock
    private CheckGeneratorService checkGeneratorService;

    @Mock
    private DatabaseUpdater databaseUpdater;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        // Инициализируем моки и создаем экземпляр TransferService перед каждым тестом
        MockitoAnnotations.initMocks(this);
        transferService = new TransferService(transactionSaver, checkGeneratorService, databaseUpdater);
    }

    @Test
    void testTransferWithinSameBankWithSufficientFunds() {
        // Создаем клиентов, банк и счета для отправителя и получателя
        Client client1 = new Client(1, "client 1");
        Client client2 = new Client(2, "client 2");
        Bank bank1 = new Bank(1, "bankA");
        Account sender = new Account(1, 1000.0, client1, bank1, LocalDateTime.now());
        Account receiver = new Account(2, 500.0, client2, bank1, LocalDateTime.now());

        // Используем doNothing() для метода, который не возвращает значение
        doNothing().when(checkGeneratorService).generateTransferCheck(
                any(), any(), any(), any(), any(), anyDouble());

        // Проверяем успешный перевод с достаточными средствами
        assertTrue(transferService.transfer(sender, receiver, 200.0));

        // Проверяем, что балансы обновлены правильно
        assertEquals(800.0, sender.getBalance());
        assertEquals(700.0, receiver.getBalance());

        // Проверяем вызовы методов для обновления баланса, сохранения транзакции и генерации чека
        verify(databaseUpdater, times(1)).updateAccountBalance2(1, 800.0, 2, 700.0);
        verify(transactionSaver, times(1)).saveTransferTransaction(sender, receiver, 200.0);
        verify(checkGeneratorService, times(1)).generateTransferCheck(
                TransactionType.TRANSFER, "bankA", "bankA", sender, receiver, 200.0);
    }

    @Test
    void testTransferWithinSameBankWithInsufficientFunds() {
        // Создаем клиентов, банк и счета для отправителя и получателя
        Client client1 = new Client(1, "client 1");
        Client client2 = new Client(2, "client 2");
        Bank bank1 = new Bank(1, "bankA");
        Account sender = new Account(1, 100.0, client1, bank1, LocalDateTime.now());
        Account receiver = new Account(2, 500.0, client2, bank1, LocalDateTime.now());

        // Проверяем, что выбрасывается InsufficientFundsException при попытке перевода с недостаточными средствами
        assertThrows(InsufficientFundsException.class, () -> transferService.transfer(sender, receiver, 200.0));

        // Проверяем, что балансы не изменились
        assertEquals(100.0, sender.getBalance());
        assertEquals(500.0, receiver.getBalance());

        // Проверяем, что методы для обновления баланса, сохранения транзакции и генерации чека не вызывались
        verify(databaseUpdater, never()).updateAccountBalance2(anyInt(), anyDouble(), anyInt(), anyDouble());
        verify(transactionSaver, never()).saveTransferTransaction(any(), any(), anyDouble());
        verify(checkGeneratorService, never()).generateTransferCheck(any(), any(), any(), any(), any(), anyDouble());
    }

    @Test
    void testTransferBetweenDifferentBanksWithSufficientFunds() {
        // Создаем клиентов, банки и счета для отправителя и получателя
        Client client1 = new Client(1, "client 1");
        Client client2 = new Client(2, "client 2");
        Bank bank1 = new Bank(1, "bankA");
        Bank bank2 = new Bank(2, "bankB");
        Account sender = new Account(1, 1000.0, client1, bank1, LocalDateTime.now());
        Account receiver = new Account(2, 500.0, client2, bank2, LocalDateTime.now());

        // Используем doNothing() для метода, который не возвращает значение
        doNothing().when(checkGeneratorService).generateTransferCheck(
                any(), any(), any(), any(), any(), anyDouble());

        assertTrue(transferService.transfer(sender, receiver, 200.0));

        // Проверяем, что балансы изменены правильно
        assertEquals(800.0, sender.getBalance());
        assertEquals(700.0, receiver.getBalance());

        // Проверяем вызовы методов для обновления баланса, сохранения транзакции и генерации чека
        verify(databaseUpdater, times(1)).updateAccountBalance2(1, 800.0, 2, 700.0);
        verify(transactionSaver, times(1)).saveTransferTransaction(sender, receiver, 200.0);
        verify(checkGeneratorService, times(1)).generateTransferCheck(
                TransactionType.TRANSFER, sender.getBank().getName(), receiver.getBank().getName(), sender, receiver, 200.0);
    }

}
