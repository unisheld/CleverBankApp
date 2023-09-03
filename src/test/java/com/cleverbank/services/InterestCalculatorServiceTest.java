package com.cleverbank.services;

import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.database.DatabaseReader;
import com.cleverbank.models.Account;
import com.cleverbank.models.Bank;
import com.cleverbank.models.Client;
import com.cleverbank.utils.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InterestCalculatorServiceTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private DatabaseUpdater databaseUpdater;

    @Mock
    private DatabaseReader databaseReader;

    private InterestCalculatorService interestCalculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        interestCalculatorService = new InterestCalculatorService(appConfig, databaseUpdater, databaseReader);
    }


    @Test
    void testCalculateInterestWithValidMonth() throws SQLException {
        // Установите месяц расчета процентов в текущий месяц
        when(appConfig.getInterestCalculationMonth()).thenReturn(Calendar.getInstance().get(Calendar.MONTH));

        Client client1 = new Client(1, "client 1");
        Client client2 = new Client(2, "client 2");
        Bank bank1 = new Bank(1, "bankA");
        Bank bank2 = new Bank(2, "bankB");

        // Создайте фиктивный список счетов
        List<Account> fakeAccounts = new ArrayList<>();
        fakeAccounts.add( new Account(1, 1000.0, client1, bank1, LocalDateTime.now()));
        fakeAccounts.add(new Account(2, 500.0, client2, bank2, LocalDateTime.now()));

        // Установите фиктивные значения для настройки процентов и интервала
        when(appConfig.getInterestRate()).thenReturn(0.05); // 5% годовых
        when(appConfig.getCheckIntervalMinutes()).thenReturn(1.0); // Проверка каждую минуту

        // Когда вызывается databaseReader.getAllAccounts(), верните фиктивный список счетов
        when(databaseReader.getAllAccounts()).thenReturn(fakeAccounts);

        // Создайте ArgumentCaptor для аргументов, переданных в databaseUpdater.updateAccountBalance
        ArgumentCaptor<Integer> accountIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Double> newBalanceCaptor = ArgumentCaptor.forClass(Double.class);

        // Вызовите метод calculateInterest() для начисления процентов
        interestCalculatorService.calculateInterest();

        // Проверьте, что метод databaseUpdater.updateAccountBalance вызывается с ожидаемыми аргументами
        verify(databaseUpdater, times(2)).updateAccountBalance(accountIdCaptor.capture(), newBalanceCaptor.capture());

        // Проверьте захваченные значения
        List<Integer> capturedAccountIds = accountIdCaptor.getAllValues();
        List<Double> capturedNewBalances = newBalanceCaptor.getAllValues();

        assertEquals(1, capturedAccountIds.get(0)); // Первый счет
        assertEquals(2, capturedAccountIds.get(1)); // Второй счет

        assertEquals(1050.0, capturedNewBalances.get(0)); // Новый баланс для первого счета
        assertEquals(525.0, capturedNewBalances.get(1)); // Новый баланс для второго счета
    }


    @Test
    void testCalculateInterestWithInvalidMonth() {
        // Установите месяц расчета процентов в следующий месяц
        when(appConfig.getInterestCalculationMonth()).thenReturn(Calendar.getInstance().get(Calendar.MONTH) + 1);

        // Не должно произойти начисление процентов, так как месяц не соответствует
        interestCalculatorService.calculateInterest();

        // Проверьте, что метод databaseUpdater.updateAccountBalance() не вызывается
        verify(databaseUpdater, never()).updateAccountBalance(anyInt(), anyDouble());
    }
}
