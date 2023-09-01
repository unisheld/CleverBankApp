package com.cleverbank.services;

import com.cleverbank.database.DatabaseConnector;
import com.cleverbank.utils.AppConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для расчета процентов по счетам.
 */
public class InterestCalculatorService {
    private final AppConfig appConfig;

    /**
     * Конструктор класса.
     *
     * @param appConfig Конфигурация приложения.
     */
    public InterestCalculatorService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

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
            // Выполняем начисление процентов
            double interestRate = appConfig.getInterestRate();

            // Настраиваем соединение с базой данных PostgreSQL
            try (Connection connection = DatabaseConnector.getConnection()) {
                // Получаем список счетов и их балансы
                String query = "SELECT id, balance FROM accounts";
                try (PreparedStatement statement = connection.prepareStatement(query);
                     ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int accountId = resultSet.getInt("id");
                        double balance = resultSet.getDouble("balance");

                        // Выполняем начисление процентов на каждом счете
                        double interest = balance * interestRate;
                        double newBalance = balance + interest;

                        // Обновляем баланс в базе данных
                        updateAccountBalance(connection, accountId, newBalance);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Проценты начислены в конце месяца.");
        }
    }

    /**
     * Обновляет баланс счета в базе данных.
     *
     * @param connection Соединение с базой данных.
     * @param accountId  Идентификатор счета.
     * @param newBalance Новый баланс счета.
     * @throws SQLException Если произошла ошибка SQL.
     */
    private void updateAccountBalance(Connection connection, int accountId, double newBalance) throws SQLException {
        // Обновляем баланс счета в базе данных
        String updateQuery = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setDouble(1, newBalance);
            updateStatement.setInt(2, accountId);
            updateStatement.executeUpdate();
        }
    }
}
