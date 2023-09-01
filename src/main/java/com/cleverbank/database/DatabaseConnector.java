package com.cleverbank.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Управляет соединением с базой данных.
 */
public class DatabaseConnector {
    private static Connection connection = null;

    /**
     * Инициализирует соединение с базой данных. Если соединение уже открыто, оно закрывается перед инициализацией нового.
     *
     * @param url      URL базы данных.
     * @param username Имя пользователя для подключения.
     * @param password Пароль пользователя для подключения.
     */
    public static void initialize(String url, String username, String password) {
        if (connection != null) {
            // Если соединение уже открыто, закрываем его перед инициализацией нового.
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает текущее соединение с базой данных.
     *
     * @return Соединение с базой данных.
     * @throws SQLException Если произошла ошибка при получении соединения или соединение не инициализировано.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Соединение не инициализировано или закрыто.");
        }
        return connection;
    }

    /**
     * Закрывает текущее соединение с базой данных, если оно открыто.
     *
     * @throws SQLException Если произошла ошибка при закрытии соединения.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
