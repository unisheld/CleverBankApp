package com.cleverbank.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс для обновления данных в базе данных.
 */
public class DatabaseUpdater {

    private Connection connection;

    /**
     * Конструктор для инициализации объекта DatabaseUpdater.
     *
     * @param connection Объект соединения с базой данных.
     */
    public DatabaseUpdater(Connection connection) {
        this.connection = connection;
    }

    /**
     * Обновляет баланс указанного счета в базе данных.
     *
     * @param accountId  Идентификатор счета для обновления.
     * @param newBalance Новый баланс счета.
     */
    public void updateAccountBalance(int accountId, double newBalance) {
        try {
            // Проверяем, существует ли счет с указанным идентификатором в базе данных
            if (accountExists(accountId)) {
                // Обновляем баланс счета в базе данных
                String updateQuery = "UPDATE accounts SET balance = ? WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setDouble(1, newBalance);
                    updateStatement.setInt(2, accountId);
                    updateStatement.executeUpdate();
                }
            } else {
                // Обработка ситуации, если счет не существует
                System.out.println("Счет с идентификатором " + accountId + " не существует в базе данных.");
            }
        } catch (SQLException e) {
            // Обработка ошибки при обновлении баланса счета
            System.err.println("Ошибка при обновлении баланса счета: " + e.getMessage());
        }
    }

    /**
     * Обновляет балансы двух счетов в базе данных.
     *
     * @param accountId1   Идентификатор первого счета для обновления.
     * @param newBalance1  Новый баланс первого счета.
     * @param accountId2   Идентификатор второго счета для обновления.
     * @param newBalance2  Новый баланс второго счета.
     */
    public void updateAccountBalance2(int accountId1, double newBalance1, int accountId2, double newBalance2) {
        try {
            // Проверяем, существуют ли счета с указанными идентификаторами в базе данных
            if (accountExists(accountId1) && accountExists(accountId2)) {
                // Обновляем балансы счетов в базе данных
                updateAccountBalance(accountId1, newBalance1);
                updateAccountBalance(accountId2, newBalance2);
            } else {
                // Обработка ситуации, если один из счетов не существует
                System.out.println("Один из счетов не существует в базе данных.");
            }
        } catch (SQLException e) {
            // Обработка ошибки при обновлении балансов счетов
            System.err.println("Ошибка при обновлении балансов счетов: " + e.getMessage());
        }
    }


    /**
     * Проверяет существование счета с указанным идентификатором в базе данных.
     *
     * @param accountId Идентификатор счета для проверки.
     * @return true, если счет существует, в противном случае false.
     * @throws SQLException Если произошла ошибка при выполнении запроса.
     */
    public boolean accountExists(int accountId) throws SQLException {
        // Проверка существования счета в базе данных
        String checkQuery = "SELECT id FROM accounts WHERE id = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, accountId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next(); // Если есть результат, счет существует
            }
        }
    }

}
