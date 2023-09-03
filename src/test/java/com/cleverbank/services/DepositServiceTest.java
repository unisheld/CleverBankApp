package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DepositServiceTest {

    @Mock
    private TransactionSaver transactionSaver;

    @Mock
    private CheckGeneratorService checkGeneratorService;

    @Mock
    private DatabaseUpdater databaseUpdater;

    @InjectMocks
    private DepositService depositService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() {
        // Создаем объект счет
        Account account = new Account(123, 1000.0,  new Client(1,"John Doe"), new Bank(1,"TEST BANK"), LocalDateTime.now());

        double amount = 500.0;

        // Вызываем метод execute
        depositService.execute(account, amount);

        // Проверяем, что методы моков были вызваны с правильными аргументами
        verify(databaseUpdater).updateAccountBalance(123, 1500.0);
        verify(transactionSaver).saveDepositTransaction(account, amount);
        verify(checkGeneratorService).generateDepositCheck("TEST BANK", account, amount);

        // Проверяем, что больше не было взаимодействий с моками
        verifyNoMoreInteractions(databaseUpdater, transactionSaver, checkGeneratorService);
    }

    @Test
    public void testExecuteWithNullAccount() {
        // Попытка выполнить операцию с null счетом
        depositService.execute(null, 500.0);

        // Проверяем, что методы моков не были вызваны
        verifyNoMoreInteractions(databaseUpdater, transactionSaver, checkGeneratorService);
    }

    @Test
    public void testExecuteWithNegativeAmount() {
        // Создаем мок объекта счета
        Account account = mock(Account.class);
        Bank bank = mock(Bank.class);
        when(account.getId()).thenReturn(123); // настройка возврата ID счета
        when(account.getBalance()).thenReturn(1000.0); // начальный баланс счета
        when(account.getBank()).thenReturn(bank); // настройка возврата мок объекта банка
        when(bank.getName()).thenReturn("TEST BANK"); // настройка возврата имени банка

        double negativeAmount = -500.0;

        // Настройка мока databaseUpdater, чтобы выбрасывать исключение при вызове
        doThrow(new RuntimeException("Unexpected call to updateAccountBalance")).when(databaseUpdater).updateAccountBalance(anyInt(), anyDouble());

        // Вызываем метод execute с отрицательной суммой
        assertThrows(RuntimeException.class, () -> depositService.execute(account, negativeAmount));

        // Проверяем, что методы моков НЕ были вызваны
        verifyNoInteractions(transactionSaver, checkGeneratorService);
    }



}
