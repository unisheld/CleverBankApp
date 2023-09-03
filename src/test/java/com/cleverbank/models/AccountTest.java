package com.cleverbank.models;

import com.cleverbank.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private Account account;

    @BeforeEach
    public void setUp() {
        // Создаем новый аккаунт перед каждым тестом
        account = new Account(1, 1000.0, new Client(1, "John"), new Bank(1, "Fake Bank"), LocalDateTime.now());
    }

    @Test
    public void testDeposit() {
        // Проверяем, что внесение средств увеличивает баланс счета
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance(), 0.01); // Допускаем погрешность 0.01
    }

    @Test
    public void testWithdrawSufficientFunds() {
        // Проверяем, что снятие средств со счета с достаточными средствами работает
        account.withdraw(500.0);
        assertEquals(500.0, account.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        // Проверяем, что снятие средств со счета с недостаточными средствами вызывает исключение
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1500.0));
    }
}
