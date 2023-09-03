package com.cleverbank.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для управления соединением с базой данных.
 */
public class DatabaseConnector {
    private static Connection connection;

    /**
     * Инициализирует соединение с базой данных. Если соединение уже открыто, оно закрывается перед инициализацией нового.
     *
     * @param url      URL базы данных.
     * @param username Имя пользователя для подключения.
     * @param password Пароль пользователя для подключения.
     * @throws SQLException Если произошла ошибка при инициализации соединения.
     */
    public static void initialize(String url, String username, String password) throws SQLException {
        // Попытка закрыть текущее соединение, если оно открыто
        closeConnection();

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            // Обработка ошибки при инициализации соединения
            System.err.println("Ошибка при инициализации соединения с базой данных: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Получает текущее соединение с базой данных.
     *
     * @return Объект соединения с базой данных.
     * @throws SQLException Если соединение не инициализировано или закрыто.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Обработка ошибки, если соединение не инициализировано или закрыто
            throw new SQLException("Соединение не инициализировано или закрыто.");
        }
        return connection;
    }

    /**
     * Закрывает текущее соединение с базой данных, если оно открыто.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Обработка ошибки при закрытии соединения
            System.err.println("Ошибка при закрытии соединения с базой данных: " + e.getMessage());
        }
    }
}
