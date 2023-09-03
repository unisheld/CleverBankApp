package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.TransactionSaver;
import com.cleverbank.exceptions.InsufficientFundsException;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.Client;
import com.cleverbank.services.CheckGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WithdrawServiceTest {

    @Mock
    private TransactionSaver transactionSaver;

    @Mock
    private CheckGeneratorService checkGeneratorService;

    @Mock
    private DatabaseUpdater databaseUpdater;

    @InjectMocks
    private WithdrawService withdrawService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteSufficientFunds() {
        // Создаем мок-счет с достаточными средствами
        Account account = new Account(1, 1000.0, new Client(1,"John Doe"), new Bank(1,"TEST BANK"), LocalDateTime.now());
        double amountToWithdraw = 500.0;

        // Устанавливаем мок-данные для databaseUpdater
        doNothing().when(databaseUpdater).updateAccountBalance(anyInt(), anyDouble());

        // Вызываем метод execute
        withdrawService.execute(account, amountToWithdraw);

        // Проверяем, что баланс счета был обновлен и транзакция была сохранена
        assertEquals(500.0, account.getBalance());
        verify(transactionSaver).saveWithdrawTransaction(account, amountToWithdraw);
        verify(checkGeneratorService).generateWithdrawCheck(anyString(), eq(account), eq(amountToWithdraw));
    }


    @Test
    public void testExecuteInsufficientFunds() {
        // Создаем мок-счет с недостаточными средствами
        Account account = new Account(1, 100.0, new Client(1,"John Doe"), new Bank(1,"TEST BANK"), LocalDateTime.now());
        double amountToWithdraw = 500.0;

        // Вызываем метод execute и ожидаем InsufficientFundsException
        assertThrows(InsufficientFundsException.class, () -> {
            withdrawService.execute(account, amountToWithdraw);
        });

        // Убеждаемся, что баланс счета не изменился и транзакция не была сохранена
        assertEquals(100.0, account.getBalance());
        verify(transactionSaver, never()).saveWithdrawTransaction(account, amountToWithdraw);
        verify(checkGeneratorService, never()).generateWithdrawCheck(anyString(), eq(account), eq(amountToWithdraw));
    }

    @Test
    public void testExecuteNullAccount() {
        // Вызываем метод execute с null счетом и ожидаем, что ничего не произойдет
        assertDoesNotThrow(() -> {
            withdrawService.execute(null, 500.0);
        });

        // Убеждаемся, что методы мока не вызывались
        verify(databaseUpdater, never()).updateAccountBalance(anyInt(), anyDouble());
        verify(transactionSaver, never()).saveWithdrawTransaction(any(), anyDouble());
        verify(checkGeneratorService, never()).generateWithdrawCheck(anyString(), any(), anyDouble());
    }
}
