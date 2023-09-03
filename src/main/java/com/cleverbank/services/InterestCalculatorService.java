package com.cleverbank.services;

import com.cleverbank.database.DatabaseConnector;
import com.cleverbank.database.DatabaseReader;
import com.cleverbank.database.DatabaseUpdater;
import com.cleverbank.models.Account;
import com.cleverbank.utils.AppConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для расчета процентов по счетам.
 */
@AllArgsConstructor
public class InterestCalculatorService {
    private final AppConfig appConfig;
    private final DatabaseUpdater databaseUpdater;
    private final DatabaseReader databaseReader;

    /**
     * Запускает расчет процентов по счетам с указанной периодичностью.
     */
    public void startInterestCalculation() {
        double checkIntervalMinutes = appConfig.getCheckIntervalMinutes();

        // Создаем планировщик задач
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Запускаем задачу по расписанию (считаем интервал в миллисекундах)
        long intervalMillis = (long) (checkIntervalMinutes * 60 * 1000);
        scheduler.scheduleAtFixedRate(this::calculateInterest, 0, intervalMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Выполняет расчет процентов и обновляет балансы счетов в конце месяца.
     */
    public void calculateInterest() {
        int interestCalculationMonth = appConfig.getInterestCalculationMonth();

        // Получаем текущий месяц (с нулевого месяца)
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        if (currentMonth == interestCalculationMonth) {
            // Выполняем начисление процентов на счетах клиентов
            double interestRate = appConfig.getInterestRate();

            try {
                // Получаем список счетов клиентов
                List<Account> allAccounts = databaseReader.getAllAccounts();

                for (Account account : allAccounts) {
                    // Выполняем начисление процентов на каждом счете
                    double balance = account.getBalance();
                    double interest = balance * interestRate;
                    double newBalance = balance + interest;

                    // Обновляем баланс в базе данных с использованием databaseUpdater
                    databaseUpdater.updateAccountBalance(account.getId(), newBalance);
                }

                System.out.println("Проценты начислены в конце месяца.");
            } catch (SQLException e) {
                // Обрабатываем исключение SQLException здесь
                e.printStackTrace(); // Выводим информацию об ошибке в консоль
            }
        }
    }
}
